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
        if (nombre.isBlank() || categoria == null || fecha == null || hora == null) {
            publicacionError = "Faltan campos obligatorios"
            return
        }

        buscandoUbicacion = true
        val db = FirebaseFirestore.getInstance()

        val actividadCloud = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "fechaHora" to LocalDateTime.of(fecha, hora).toString(),
            "ubicacion" to ubicacion,
            "latitud" to latitud,
            "longitud" to longitud,
            "idCreador" to usuarioActualId,
            "categoria" to categoria?.name,
            "fechaCreacion" to LocalDateTime.now().toString()
        )

        db.collection("actividades")
            .add(actividadCloud)
            .addOnSuccessListener { documentReference ->
                viewModelScope.launch(Dispatchers.IO) {
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
                            idInteres = (categoria?.ordinal ?: 0) + 1,
                            imageUrl = null
                        )
                        actividadRepository.crearActividad(nuevaActividad)

                        withContext(Dispatchers.Main) {
                            publicacionExitosa = true
                            buscandoUbicacion = false
                        }
                    } catch (roomEx: Exception) {
                        Log.e("ROOM_ERROR", "Fallo al guardar localmente: ${roomEx.message}")
                        withContext(Dispatchers.Main) {
                            publicacionExitosa = true
                            buscandoUbicacion = false
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_ERROR", "Fallo al subir a la nube: ${e.message}")
                publicacionError = "Error al sincronizar con la nube: ${e.message}"
                buscandoUbicacion = false
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