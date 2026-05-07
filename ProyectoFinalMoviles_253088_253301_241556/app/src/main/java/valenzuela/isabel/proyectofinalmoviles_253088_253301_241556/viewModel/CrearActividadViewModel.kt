package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import java.time.LocalDate
import java.time.LocalTime

class CrearActividadViewModel : ViewModel() {

    var nombre by mutableStateOf("")
        private set
    var categoria by mutableStateOf<Interes?>(null)
        private set
    var descripcion by mutableStateOf("")
        private set
    var ubicacion by mutableStateOf("")
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

    var errorFechaLimite by mutableStateOf<String?>(null)



    fun onNombreChange(value: String) { nombre = value }
    fun onCategoriaChange(value: Interes) { categoria = value }
    fun onDescripcionChange(value: String) { descripcion = value }
    fun onUbicacionChange(value: String) { ubicacion = value }
    fun onFechaChange(value: LocalDate) { fecha = value }
    fun onHoraChange(value: LocalTime) { hora = value }
    fun onFechaLimiteChange(nuevaFechaLimite: LocalDate) {
        val fechaEvento = fecha
        if (fechaEvento != null && nuevaFechaLimite.isAfter(fechaEvento)) {
            var errorFechaLimite by mutableStateOf<String?>(null)
            errorFechaLimite = "La fecha límite no puede ser posterior a la fecha de la actividad"
        } else {
            fechaLimite = nuevaFechaLimite
            errorFechaLimite = null
        }
    }
    fun onMaxParticipantesChange(value: String) { maxParticipantes = value }
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