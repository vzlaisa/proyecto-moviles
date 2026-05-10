package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api.NominatimResponse
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api.RetrofitClient
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EditarActividadViewModel(private val actividadRepository: ActividadRepository): ViewModel() {

    private var actividadOriginal: ActividadEntity? = null
    
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
    var urlImagenActual by mutableStateOf<String?>(null)
        private set

    var queryBusqueda by mutableStateOf("")
        private set
    var resultadosBusqueda by mutableStateOf<List<NominatimResponse>>(emptyList())
        private set
    var buscandoUbicacion by mutableStateOf(false)
        private set

    var actualizacionExitosa by mutableStateOf(false)
        private set
    var actualizacionError by mutableStateOf<String?>(null)
        private set

    fun cargarActividad(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val act = actividadRepository.getActividadById(id)
                actividadOriginal = act

                withContext(Dispatchers.Main) {
                    nombre = act.nombre
                    // Como en crear hiciste "categoria!!.ordinal + 1", aquí hacemos lo inverso
                    categoria = Interes.entries.getOrNull(act.idInteres - 1)
                    descripcion = act.descripcion
                    ubicacion = act.ubicacion
                    latitud = act.latitud
                    longitud = act.longitud

                    fecha = act.fechaHora.toLocalDate()
                    hora = act.fechaHora.toLocalTime()
                    fechaLimite = act.fechaLimite?.toLocalDate()

                    maxParticipantes = act.maxParticipantes.toString()
                    isPrivada = !act.publica
                    isRecurrente = act.recurrente
                    urlImagenActual = act.imageUrl
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    actualizacionError = "Error al cargar los datos: ${e.message}"
                }
            }
        }
    }

    fun onNombreChange(value: String) { nombre = value }
    fun onCategoriaChange(value: Interes) { categoria = value }
    fun onDescripcionChange(value: String) { descripcion = value }

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
        }
    }
    fun onPrivadaChange(value: Boolean) { isPrivada = value }
    fun onRecurrenteChange(value: Boolean) { isRecurrente = value }
    fun onFotoChange(uri: Uri?) { fotoUri = uri }

    fun guardarCambios(context: Context) {
        actualizacionError = null

        val original = actividadOriginal
        if (original == null) {
            actualizacionError = "Error: La actividad no está cargada."
            return
        }

        if (nombre.isBlank() || categoria == null || fecha == null || hora == null) {
            actualizacionError = "Faltan campos obligatorios"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localImagePath = if (fotoUri != null) {
                    guardarImagenEnLocal(context, fotoUri!!)
                } else {
                    urlImagenActual
                }

                val actividadActualizada = original.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaHora = LocalDateTime.of(fecha, hora),
                    fechaLimite = fechaLimite?.atTime(23, 59),
                    ubicacion = ubicacion,
                    latitud = latitud,
                    longitud = longitud,
                    maxParticipantes = maxParticipantes.toIntOrNull() ?: original.maxParticipantes,
                    publica = !isPrivada,
                    recurrente = isRecurrente,
                    idInteres = categoria!!.ordinal + 1,
                    imageUrl = localImagePath
                )

                actividadRepository.actualizarActividad(actividadActualizada)

                withContext(Dispatchers.Main) {
                    actualizacionExitosa = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    actualizacionExitosa = false
                    actualizacionError = e.message
                }
            }
        }
    }

    private fun guardarImagenEnLocal(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "actividad_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            null
        }
    }

    fun limpiarDatos() {
        actualizacionExitosa = false
        actualizacionError = null
        fotoUri = null
    }
}