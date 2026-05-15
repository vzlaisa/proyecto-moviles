package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.InscripcionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionConUsuario
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion

class InscripcionRepository(private val dao: InscripcionDAO) {

    suspend fun unirse(idActividad: Int, idUsuario: Int, esPublica: Boolean) {
        val existente = dao.getInscripcion(idActividad, idUsuario)
        if (existente?.estado == EstadoInscripcion.ASISTENCIA_CONFIRMADA) {
            throw Exception("Ya confirmaste asistencia")
        }

        val estado = if (esPublica) EstadoInscripcion.CONFIRMADO else EstadoInscripcion.PENDIENTE
        dao.insertar(InscripcionEntity(idUsuario = idUsuario, idActividad = idActividad, estado = estado))
    }

    suspend fun abandonar(idActividad: Int, idUsuario: Int) = withContext(Dispatchers.IO) {
        dao.eliminar(idActividad, idUsuario)
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
    }

    suspend fun actualizarEstado(idActividad: Int, idUsuario: Int, estado: EstadoInscripcion) {
        dao.actualizarEstado(idActividad, idUsuario, estado)
    }

}