package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

class CrearActividadViewModel(
    private val actividadRepository: ActividadRepository,
    private val usuarioActualId: Int
) : ViewModel() {

    var nombre by mutableStateOf("")
        private set
    var categoria by mutableStateOf<Interes?>(null)
        private set
    var descripcion by mutableStateOf("")
        private set
    var ubicacion by mutableStateOf("")
        private set

    var latitud by mutableStateOf(0.0)
        private set

    var longitud by mutableStateOf(0.0)
        private set

    var fecha by mutableStateOf<LocalDate?>(null)
        private set
    var hora by mutableStateOf<LocalTime?>(null)
        private set
    var fechaLimite by mutableStateOf<LocalDate?>(null)
        private set

    var maxParticipantes by mutableStateOf("")
        private set
    var isPrivada by mutableStateOf(false)
        private set
    var isRecurrente by mutableStateOf(false)
        private set
    var fotoUri by mutableStateOf<Uri?>(null)
        private set

    var publicacionExitosa by mutableStateOf(false)
        private set
    var publicacionError by mutableStateOf<String?>(null)
        private set



    fun onNombreChange(value: String) { nombre = value }
    fun onCategoriaChange(value: Interes) { categoria = value }
    fun onDescripcionChange(value: String) { descripcion = value }
    fun onUbicacionSeleccionada(nombre: String, lat: Double, lon: Double) {
        ubicacion = nombre
        latitud = lat
        longitud = lon
    }
    fun onFechaChange(value: LocalDate) { fecha = value }
    fun onHoraChange(value: LocalTime) { hora = value }
    fun onFechaLimiteChange(value: LocalDate) { fechaLimite = value }
    fun onMaxParticipantesChange(value: String) {
        if (value.all { it.isDigit() }) {
            maxParticipantes = value
        } }
    fun onPrivadaChange(value: Boolean) { isPrivada = value }
    fun onRecurrenteChange(value: Boolean) { isRecurrente = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }

    fun publicarActividad() {
        publicacionError = null

        if (nombre.isBlank() || categoria == null || fecha == null || hora == null) {
            publicacionError = "Faltan campos obligatorios"
            return
        }

        viewModelScope.launch {
            try {
                val nuevaActividad = ActividadEntity(
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaHora = LocalDateTime.of(fecha, hora),
                    fechaLimite = fechaLimite?.atTime(23, 59),
                    fechaCreacion = LocalDateTime.now(),
                    ubicacion = ubicacion,
                    latitud = latitud,
                    longitud = longitud,
                    maxParticipantes = maxParticipantes.toIntOrNull() ?: 1,
                    publica = !isPrivada,
                    recurrente = isRecurrente,
                    idCreador = usuarioActualId,
                    idInteres = categoria!!.ordinal + 1
                )

                actividadRepository.crearActividad(nuevaActividad)
                publicacionExitosa = true
            } catch (e: Exception) {
                publicacionExitosa = false
                publicacionError = e.message
            }
        }
    }



    fun limpiarDatos() {
        nombre = ""
        categoria = null
        descripcion = ""
        ubicacion = ""

        fecha = null
        hora = null
        fechaLimite = null

        maxParticipantes = ""
        isPrivada = false
        isRecurrente = false
        fotoUri = null

        publicacionExitosa = false
        publicacionError = null
    }
}