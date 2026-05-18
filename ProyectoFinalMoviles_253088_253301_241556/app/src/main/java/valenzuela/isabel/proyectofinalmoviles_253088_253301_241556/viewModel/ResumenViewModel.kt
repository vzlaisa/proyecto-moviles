package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository

class ResumenViewModel(private val dataStore: DataStoreManager, private val repository: ActividadRepository): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val topIntereses = dataStore.usuarioIdFlow
        .flatMapLatest { idUsuario ->
            if (idUsuario > 0) repository.getTop3InteresesDelMes(idUsuario)
            else flowOf(emptyList())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val nuevasPersonas = dataStore.usuarioIdFlow
        .flatMapLatest { idUsuario ->
            if (idUsuario > 0) repository.getNuevasPersonasDelMes(idUsuario)
            else flowOf(emptyList())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}