package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.NotificacionRepository

class NotificacionViewModel(private val dataStore: DataStoreManager, private val repository: NotificacionRepository): ViewModel() {

    private val usuarioActualId = dataStore.usuarioIdFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val notificaciones = usuarioActualId.flatMapLatest { id ->
        if (id == -1) {
            flowOf(emptyList())
        } else {
            // Cargar las notificaciones
            repository.getNotificaciones(id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val badgeCount = usuarioActualId.flatMapLatest { id ->
        if (id == -1) {
            flowOf(0)
        } else {
            repository.getNoLeidas(id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun marcarComoLeida(id: Int) {
        viewModelScope.launch {
            repository.marcarComoLeida(id)
        }
    }

    fun marcarTodoComoLeido() {
        viewModelScope.launch {
            val currentId = dataStore.usuarioIdFlow.first()
            if (currentId != -1) {
                repository.marcarTodasComoLeidas(currentId)
            }
        }
    }

    fun eliminarNotificacion(id: Int) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }
}