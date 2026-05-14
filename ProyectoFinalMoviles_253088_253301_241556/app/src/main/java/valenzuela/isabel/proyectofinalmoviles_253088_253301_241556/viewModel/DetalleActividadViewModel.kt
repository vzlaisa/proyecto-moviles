package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionConUsuario
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.InscripcionRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.state.UiEstado

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

    private val _participantes = MutableStateFlow<List<InscripcionConUsuario>>(emptyList())
    val participantes = _participantes.asStateFlow()

    private val _uiEstado = MutableStateFlow<UiEstado>(UiEstado.Idle)
    val uiEstado = _uiEstado.asStateFlow()

    private var participantesJob: Job? = null

    // Separar participantes confirmados de los pendientes
    val participantesConfirmados = _participantes.map { lista ->
        lista.filter { it.inscripcion.estado != EstadoInscripcion.PENDIENTE }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val solicitudesPendientes = _participantes.map { lista ->
        lista.filter { it.inscripcion.estado == EstadoInscripcion.PENDIENTE }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun cargar(actividad: ActividadConDetalle) {
        // Evita recargar si es la misma actividad
        if (_actividad.value?.actividad?.id == actividad.actividad.id) return
        _actividad.value = actividad

        viewModelScope.launch {
            // Estado de inscripción del usuario actual
            _estadoInscripcion.value = inscripcionRepository.getEstadoInscripcion(
                actividad.actividad.id,
                usuarioActualId.value
            )
        }

        // Participantes en tiempo real
        participantesJob?.cancel()
        participantesJob = viewModelScope.launch {
            inscripcionRepository.getParticipantesConNombre(actividad.actividad.id)
                .collect { _participantes.value = it }
        }
    }

    fun unirse() {
        val actividad = _actividad.value ?: return
        val idUsuario = usuarioActualId.value

        if (idUsuario == 0) {
            _uiEstado.value = UiEstado.Error("Sesión no válida")
            return
        }

        viewModelScope.launch {
            try {
                _uiEstado.value = UiEstado.Cargando
                inscripcionRepository.unirse(
                    idActividad = actividad.actividad.id,
                    idUsuario = idUsuario,
                    esPublica = actividad.actividad.publica
                )
                _estadoInscripcion.value =
                    if (actividad.actividad.publica) EstadoInscripcion.CONFIRMADO
                    else EstadoInscripcion.PENDIENTE
                _uiEstado.value = UiEstado.Idle
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error(e.message ?: "No se pudo completar la inscripción")
            }
        }
    }

    fun abandonar() {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                _uiEstado.value = UiEstado.Cargando
                inscripcionRepository.abandonar(actividad.actividad.id, usuarioActualId.value)
                _estadoInscripcion.value = null
                _uiEstado.value = UiEstado.Idle
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo abandonar la actividad")
            }
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

    fun confirmarAsistencia() {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                _uiEstado.value = UiEstado.Cargando
                inscripcionRepository.confirmarAsistencia(
                    actividad.actividad.id,
                    usuarioActualId.value
                )
                _estadoInscripcion.value = EstadoInscripcion.ASISTENCIA_CONFIRMADA
                _uiEstado.value = UiEstado.Idle
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo confirmar la asistencia")
            }
        }
    }

    fun expulsarParticipante(idUsuario: Int) {
        val actividad = _actividad.value ?: return
        Log.d("EXPULSAR", "Intentando expulsar usuario $idUsuario")
        viewModelScope.launch {
            try {
                inscripcionRepository.abandonar(actividad.actividad.id, idUsuario)
                Log.d("EXPULSAR", "Usuario expulsado")
            } catch (e: Exception) {
                Log.e("EXPULSAR", e.message ?: "Error")
                _uiEstado.value = UiEstado.Error("No se pudo eliminar al participante")
            }
        }
    }

    fun aceptarSolicitud(idUsuario: Int) {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                inscripcionRepository.actualizarEstado(
                    actividad.actividad.id,
                    idUsuario,
                    EstadoInscripcion.CONFIRMADO
                )
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo aceptar la solicitud")
            }
        }
    }

    fun rechazarSolicitud(idUsuario: Int) {
        val actividad = _actividad.value ?: return
        viewModelScope.launch {
            try {
                inscripcionRepository.abandonar(actividad.actividad.id, idUsuario)
            } catch (e: Exception) {
                _uiEstado.value = UiEstado.Error("No se pudo rechazar la solicitud")
            }
        }
    }

    fun limpiar() {
        _actividad.value = null
        _estadoInscripcion.value = null
        _participantes.value = emptyList()
        _uiEstado.value = UiEstado.Idle
        participantesJob?.cancel()
    }
}