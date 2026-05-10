package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import java.time.LocalDate
import java.time.LocalDateTime

class ActividadRepository(private val actividadDAO: ActividadDAO) {

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

        return actividadDAO.insertActividad(actividad)
    }

    suspend fun eliminarActividad(actividad: ActividadEntity) {
        actividadDAO.deleteActividad(actividad)
    }

    suspend fun getActividadById(id: Int): ActividadEntity {
        return actividadDAO.getById(id)
    }

    suspend fun actualizarActividad(actividad: ActividadEntity) {
        if (actividad.nombre.isBlank()) throw ValidationException("El nombre de la actividad es obligatorio")
        if (actividad.nombre.length < 5) throw ValidationException("El nombre debe tener al menos 5 caracteres")
        if (actividad.ubicacion.isBlank()) throw ValidationException("La ubicación es obligatoria")
        if (actividad.descripcion.isBlank()) throw ValidationException("La descripción es obligatoria")
        if (actividad.maxParticipantes <= 0) throw ValidationException("Debe haber al menos 1 participante")

        actividadDAO.updateActividad(actividad)
    }

}