package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresConteo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NuevaPersona
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import java.time.LocalDate
import java.time.LocalDateTime

class ActividadRepository(private val actividadDAO: ActividadDAO) {

    private val firestore = FirebaseFirestore.getInstance()

    fun getActividades(): Flow<List<ActividadConDetalle>> {
        return actividadDAO.getActividades()
    }

    fun getActividadesFiltradas(
        idInteres: Int?,
        busqueda: String,
        fecha: LocalDate?
    ): Flow<List<ActividadConDetalle>> {
        return actividadDAO.getActividadesFiltradas(
            idInteres = idInteres, texto= busqueda, fecha = fecha
        )
    }

    fun getTop3InteresesDelMes(idUsuario: Int): Flow<List<InteresConteo>> {
        return actividadDAO.getTop3InteresesDelMes(idUsuario)
    }

    fun getNuevasPersonasDelMes(idUsuario: Int): Flow<List<NuevaPersona>> {
        return actividadDAO.getNuevasPersonasDelMes(idUsuario)
    }

    suspend fun crearActividad(actividad: ActividadEntity): Long {
        // Validaciones para nombre
        if (actividad.nombre.isBlank()) throw ValidationException("El nombre de la actividad es obligatorio")
        if (actividad.nombre.length < 5) throw ValidationException("El nombre debe tener al menos 5 caracteres")

        // Validaciones para fecha
        if (actividad.fechaHora.isBefore(LocalDateTime.now())) throw ValidationException("La actividad no puede ser en el pasado")

        // Validaciones para fecha límite
        actividad.fechaLimite?.let { limite ->
            if (limite.isAfter(actividad.fechaHora)) throw ValidationException("La fecha límite no puede ser después del evento")
            if (limite.isBefore(LocalDateTime.now())) throw ValidationException("La fecha límite ya pasó")
        }

        // Validaciones para ubicación
        if (actividad.ubicacion.isBlank()) throw ValidationException("La ubicación es obligatoria")

        // Validaciones para descripción
        if (actividad.descripcion.isBlank()) throw ValidationException("La descripción de la actividad es obligatoria")
        if (actividad.descripcion.length < 10) throw ValidationException("La descripción debe ser más detallada")

        // Validaciones para participantes
        if (actividad.maxParticipantes <= 0) throw ValidationException("Debe haber al menos 1 participante")
        if (actividad.maxParticipantes > 100) throw ValidationException("Demasiados participantes permitidos")

        // Validaciones para relaciones
        if (actividad.idCreador <=0) throw ValidationException("El id del creador es inválido")
        if (actividad.idInteres <=0) throw ValidationException("El id del interés es inválido")

        // Registrar en room
        val id = actividadDAO.insertActividad(actividad)

        // Registrar en firebase
        try {
            firestore.collection("actividades")
                .document(id.toString())
                .set(mapOf(
                    "id" to id,
                    "nombre" to actividad.nombre,
                    "descripcion" to actividad.descripcion,
                    "fechaHora" to actividad.fechaHora.toString(),
                    "fechaLimite" to actividad.fechaLimite?.toString(),
                    "fechaCreacion" to actividad.fechaCreacion.toString(),
                    "ubicacion" to actividad.ubicacion,
                    "latitud" to actividad.latitud,
                    "longitud" to actividad.longitud,
                    "maxParticipantes" to actividad.maxParticipantes,
                    "publica" to actividad.publica,
                    "recurrente" to actividad.recurrente,
                    "idCreador" to actividad.idCreador,
                    "idInteres" to actividad.idInteres,
                    "imageUrl" to actividad.imageUrl
                )).await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al crear actividad, se sincronizará después: ${e.message}")
        }

        return id
    }

    suspend fun eliminarActividad(actividad: ActividadEntity) {
        actividadDAO.deleteActividad(actividad)

        try {
            firestore.collection("actividades")
                .document(actividad.id.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al eliminar actividad, se sincronizará después: ${e.message}")
        }
    }

    suspend fun getActividadById(id: Int): ActividadEntity {
        return actividadDAO.getById(id)
    }

    suspend fun limpiarActividadesLocales() {
        actividadDAO.limpiarActividadesLocales()
    }

    suspend fun actualizarActividad(actividad: ActividadEntity) {
        if (actividad.nombre.isBlank()) throw ValidationException("El nombre de la actividad es obligatorio")
        if (actividad.nombre.length < 5) throw ValidationException("El nombre debe tener al menos 5 caracteres")
        if (actividad.ubicacion.isBlank()) throw ValidationException("La ubicación es obligatoria")
        if (actividad.descripcion.isBlank()) throw ValidationException("La descripción es obligatoria")
        if (actividad.maxParticipantes <= 0) throw ValidationException("Debe haber al menos 1 participante")

        actividadDAO.updateActividad(actividad)

        try {
            firestore.collection("actividades")
                .document(actividad.id.toString())
                .update(mapOf(
                    "nombre" to actividad.nombre,
                    "descripcion" to actividad.descripcion,
                    "fechaHora" to actividad.fechaHora.toString(),
                    "fechaLimite" to actividad.fechaLimite?.toString(),
                    "ubicacion" to actividad.ubicacion,
                    "latitud" to actividad.latitud,
                    "longitud" to actividad.longitud,
                    "maxParticipantes" to actividad.maxParticipantes,
                    "publica" to actividad.publica,
                    "recurrente" to actividad.recurrente,
                    "idInteres" to actividad.idInteres,
                    "imageUrl" to actividad.imageUrl
                )).await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al actualizar actividad, se sincronizará después: ${e.message}")
        }
    }
}