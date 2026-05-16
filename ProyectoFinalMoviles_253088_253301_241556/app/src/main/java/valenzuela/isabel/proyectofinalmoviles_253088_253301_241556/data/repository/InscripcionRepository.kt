package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.InscripcionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionConUsuario
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.TipoNotificacion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import java.time.LocalDateTime

class InscripcionRepository(private val dao: InscripcionDAO, private val actividadDAO: ActividadDAO) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun unirse(idActividad: Int, idUsuario: Int, esPublica: Boolean) {
        val actividad = actividadDAO.getById(idActividad)
        val participantesActuales = dao.contarParticipantesConfirmados(idActividad)

        if (participantesActuales >= actividad.maxParticipantes) {
            throw ValidationException("La actividad ya está llena")
        }

        val estado = if (esPublica) EstadoInscripcion.CONFIRMADO else EstadoInscripcion.PENDIENTE
        val inscripcion = InscripcionEntity(idUsuario = idUsuario, idActividad = idActividad, estado = estado)

        // Insertar en Room
        dao.insertar(inscripcion)

        // Insertar en Firebase
        try {
            firestore.collection("inscripciones")
                .document("${idActividad}_${idUsuario}")
                .set(
                    mapOf(
                        "idActividad" to idActividad,
                        "idUsuario" to idUsuario,
                        "estado" to estado.name,
                        "fechaInscripcion" to inscripcion.fechaInscripcion.toString()
                    )
                ).await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al guardar inscripción: ${e.message}")
        }

        if (esPublica) {
            // Notificar al creador
            crearNotificacion(
                idUsuario = actividad.idCreador,
                titulo = "Nuevo participante",
                mensaje = "Alguien se unió a '${actividad.nombre}'",
                tipo = TipoNotificacion.NUEVO_PARTICIPANTE,
                idActividad = idActividad
            )

        } else {
            // Notificar al creador
            crearNotificacion(
                idUsuario = actividad.idCreador,
                titulo = "Nueva solicitud",
                mensaje = "Alguien quiere unirse a '${actividad.nombre}'",
                tipo = TipoNotificacion.NUEVA_SOLICITUD,
                idActividad = idActividad
            )
        }


//        val existente = dao.getInscripcion(idActividad, idUsuario)
//        if (existente?.estado == EstadoInscripcion.ASISTENCIA_CONFIRMADA) {
//            throw Exception("Ya confirmaste asistencia")
//        }
//
//        val estado = if (esPublica) EstadoInscripcion.CONFIRMADO else EstadoInscripcion.PENDIENTE
//        dao.insertar(InscripcionEntity(idUsuario = idUsuario, idActividad = idActividad, estado = estado))
    }

    suspend fun abandonar(idActividad: Int, idUsuario: Int, tipoNotificacion: TipoNotificacion? = null) = withContext(Dispatchers.IO) {
        val actividad = actividadDAO.getById(idActividad)

        // Eliminar en Room
        dao.eliminar(idActividad, idUsuario)

        // Eliminar en Firebase
        try {
            firestore.collection("inscripciones")
                .document("${idActividad}_${idUsuario}")
                .delete()
                .await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al eliminar inscripción: ${e.message}")
        }

        // Crear notificación si aplica
        when (tipoNotificacion) {
            TipoNotificacion.SOLICITUD_RECHAZADA -> {
                crearNotificacion(
                    idUsuario = idUsuario,
                    titulo = "Solicitud rechazada",
                    mensaje = "Tu solicitud fue rechazada en '${actividad.nombre}'",
                    tipo = TipoNotificacion.SOLICITUD_RECHAZADA,
                    idActividad = idActividad
                )
            }

            TipoNotificacion.PARTICIPANTE_EXPULSADO -> {
                crearNotificacion(
                    idUsuario = idUsuario,
                    titulo = "Has sido removido",
                    mensaje = "Fuiste removido de '${actividad.nombre}'",
                    tipo = TipoNotificacion.MENSAJE_SISTEMA,
                    idActividad = idActividad
                )
            }

            else -> Unit
        }
    }

    suspend fun getEstadoInscripcion(idActividad: Int, idUsuario: Int): EstadoInscripcion? = withContext(Dispatchers.IO) {
        dao.getInscripcion(idActividad, idUsuario)?.estado
    }

    suspend fun getParticipantesActivos(idActividad: Int): Flow<List<InscripcionEntity>> {
        return dao.getParticipantesActivos(idActividad)
    }

    fun getParticipantesConNombre(idActividad: Int): Flow<List<InscripcionConUsuario>> {
        return dao.getParticipantesConNombre(idActividad)
    }

    suspend fun confirmarAsistencia(idActividad: Int, idUsuario: Int) {
        dao.actualizarEstado(idActividad, idUsuario, EstadoInscripcion.ASISTENCIA_CONFIRMADA)

        try {
            firestore.collection("inscripciones")
                .document("${idActividad}_${idUsuario}")
                .update(
                    "estado",
                    EstadoInscripcion.ASISTENCIA_CONFIRMADA.name
                ).await()
        } catch(e: Exception) {
            Log.w("SYNC", "Sin red al confirmar asistencia: ${e.message}")
        }
    }

    suspend fun actualizarEstado(idActividad: Int, idUsuario: Int, estado: EstadoInscripcion) {
        dao.actualizarEstado(idActividad, idUsuario, estado)

        try {
            firestore.collection("inscripciones")
                .document("${idActividad}_${idUsuario}")
                .update("estado", estado.name).await()
        } catch(e: Exception) {
            Log.w("SYNC", "Sin red al actualizar estado: ${e.message}")
        }

        val actividad = actividadDAO.getById(idActividad)

        when (estado) {
            EstadoInscripcion.CONFIRMADO -> {
                crearNotificacion(
                    idUsuario = idUsuario,
                    titulo = "Solicitud aceptada",
                    mensaje = "Fuiste aceptado en '${actividad.nombre}'",
                    tipo = TipoNotificacion.SOLICITUD_ACEPTADA,
                    idActividad = idActividad
                )
            }

            else -> Unit
        }
    }

    private suspend fun crearNotificacion(
        idUsuario: Int,
        titulo: String,
        mensaje: String,
        tipo: TipoNotificacion,
        idActividad: Int?
    ) {
        try {
            firestore.collection("notificaciones")
                .add(
                    mapOf(
                        "idUsuario" to idUsuario,
                        "titulo" to titulo,
                        "mensaje" to mensaje,
                        "tipo" to tipo.name,
                        "idActividad" to idActividad,
                        "fecha" to LocalDateTime.now().toString(),
                        "leida" to false
                    )
                ).await()
        } catch (e: Exception) {
            Log.w("SYNC", "Error al crear notificación: ${e.message}")
        }
    }

}