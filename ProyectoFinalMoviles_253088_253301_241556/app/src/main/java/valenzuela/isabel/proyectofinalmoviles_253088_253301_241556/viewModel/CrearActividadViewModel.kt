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
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api.NominatimResponse
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api.RetrofitClient
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.ImageUploader

class CrearActividadViewModel(private val dataStore: DataStoreManager, private val actividadRepository: ActividadRepository): ViewModel() {

    // Obtiene el id del usuario
    val usuarioActualId: Int
        get() = _usuarioActualId.value

    private val _usuarioActualId = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            dataStore.usuarioIdFlow.collect { id ->
                _usuarioActualId.value = id
            }
        }
    }

    // Estados del formulario
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
    var publicando by mutableStateOf(false)
        private set

    // Estados de búsqueda para ubicación
    var queryBusqueda by mutableStateOf("")
        private set
    var resultadosBusqueda by mutableStateOf<List<NominatimResponse>>(emptyList())
        private set
    var buscandoUbicacion by mutableStateOf(false)
        private set

    // Estados de control para las screens
    var publicacionExitosa by mutableStateOf(false)
        private set
    var publicacionError by mutableStateOf<String?>(null)
        private set

    fun onNombreChange(value: String) { nombre = value }
    fun onCategoriaChange(value: Interes) { categoria = value }
    fun onDescripcionChange(value: String) { descripcion = value }

    // Lógica del Buscador
    fun onQueryBusquedaChange(nuevoTexto: String) {
        queryBusqueda = nuevoTexto
        if (nuevoTexto.length > 3) {
            buscarUbicacion(nuevoTexto)
        } else {
            resultadosBusqueda = emptyList()
        }
    }

    private var searchJob: Job? = null
    private fun buscarUbicacion(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            buscandoUbicacion = true
            try {
                val resultados = RetrofitClient.nominatimService.buscarLugar(query)
                // Volver al hilo principal para actualizar la lista de UI
                withContext(Dispatchers.Main) {
                    resultadosBusqueda = resultados
                }
            } catch (e: Exception) {
                Log.e("NOMINATIM_ERROR", "Fallo al buscar: ${e.message}")
            } finally {
                buscandoUbicacion = false
            }
        }
    }

    fun onUbicacionSeleccionada(nombre: String, lat: Double, lon: Double) {
        ubicacion = nombre
        latitud = lat
        longitud = lon

        queryBusqueda = ""
        resultadosBusqueda = emptyList()
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

    fun publicarActividad(context: Context) {
        if (publicando) return

        if (nombre.isBlank() || categoria == null || fecha == null || hora == null) {
            publicacionError = "Faltan campos obligatorios"
            return
        }

        publicando = true
        publicacionError = null

        val appContext = context.applicationContext
        val fotoSeleccionada = fotoUri

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val urlFoto: String? = fotoSeleccionada?.let { uri ->
                    try {
                        ImageUploader.subirFotoActividad(
                            context = appContext,
                            uri = uri,
                            idCreador = usuarioActualId
                        )
                    } catch (e: Exception) {
                        Log.e("STORAGE_ERROR", "Fallo al subir la foto: ${e.message}")
                        throw IllegalStateException(
                            "No se pudo subir la foto. Verifica tu conexión e inténtalo de nuevo."
                        )
                    }
                }

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
                    idInteres = (categoria?.ordinal ?: 0) + 1,
                    imageUrl = urlFoto
                )

                actividadRepository.crearActividad(nuevaActividad)

                withContext(Dispatchers.Main) {
                    publicacionExitosa = true
                    publicando = false
                }
            } catch (ve: ValidationException) {
                withContext(Dispatchers.Main) {
                    publicacionError = ve.message
                    publicando = false
                }
            } catch (e: Exception) {
                Log.e("PUBLICAR_ERROR", "Fallo al publicar actividad: ${e.message}")
                withContext(Dispatchers.Main) {
                    publicacionError = e.message ?: "Error al publicar la actividad"
                    publicando = false
                }
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
        publicando = false
    }
}