package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.InscripcionRepository

class DetalleActividadViewModel(
    private val actividadRepository: ActividadRepository,
    private val inscripcionRepository: InscripcionRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _actividad = MutableStateFlow<ActividadConDetalle?>(null)
    val actividad = _actividad.asStateFlow()

    val usuarioActualId = dataStore.usuarioIdFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    private val _estadoInscripcion = MutableStateFlow<EstadoInscripcion?>(null)
    val estadoInscripcion = _estadoInscripcion.asStateFlow()

    private val _participantes = MutableStateFlow<List<InscripcionEntity>>(emptyList())
    val participantes = _participantes.asStateFlow()

    private val _uiEstado = MutableStateFlow<UiEstado>(UiEstado.Idle)
    val uiEstado = _uiEstado.asStateFlow()

    fun cargar(actividad: ActividadConDetalle) {
        _actividad.value = actividad
        val idUsuario = usuarioActualId.value
        val idActividad = actividad.actividad.id

        viewModelScope.launch {
            // Estado de inscripción del usuario actual
            _estadoInscripcion.value = inscripcionRepository.getEstadoInscripcion(idActividad, idUsuario)
            // Participantes activos en tiempo real
            inscripcionRepository.getParticipantesActivos(idActividad).collect {
                _participantes.value = it
            }
        }
    }

    fun unirse() {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                inscripcionRepository.unirse(
                    idActividad = actividad.actividad.id,
                    idUsuario = usuarioActualId.value,
                    esPublica = actividad.actividad.publica
                )

                _estadoInscripcion.value =
                    if (actividad.actividad.publica) EstadoInscripcion.CONFIRMADO
                    else EstadoInscripcion.PENDIENTE
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo completar la inscripción")
            }
        }
    }

    fun abandonar() {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            inscripcionRepository.abandonar(actividad.actividad.id, usuarioActualId.value)
            _estadoInscripcion.value = null
        }
    }

    fun eliminarActividad(onExito: () -> Unit) {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                actividadRepository.eliminarActividad(actividad.actividad)
                onExito()
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo eliminar la actividad")
            }
        }
    }
}

sealed class UiEstado {
    object Idle : UiEstado()
    object Cargando : UiEstado()
    data class Error(val mensaje: String) : UiEstado()
}