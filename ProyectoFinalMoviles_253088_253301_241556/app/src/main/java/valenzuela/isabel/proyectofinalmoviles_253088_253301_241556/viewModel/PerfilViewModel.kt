package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository

class PerfilViewModel(private val dataStore: DataStoreManager, private val repository: UsuarioRepository): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario = dataStore.nicknameInFlow
        .flatMapLatest { nickname ->
            if (nickname.isBlank()) {
                flowOf()
            } else {
                // Busca al usuario en el repositorio cada vez que el nickname cambie
                flow {
                    emit(repository.getByIdentificador(nickname))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


}