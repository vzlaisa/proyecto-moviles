package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.FiltrosActividades
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel(
    private val repository: ActividadRepository,
    private val dataStore: DataStoreManager
): ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosActividades())
    val filtros = _filtros.asStateFlow()

    val nickname = dataStore.nicknameInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val actividades = _filtros
        .flatMapLatest { filtros ->
            repository.getActividadesFiltradas(
                idInteres = filtros.idInteres,
                busqueda = filtros.textoBusqueda,
                fecha = filtros.fecha
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Actualizadores de filtro
    fun setInteres(id: Int?) {
        _filtros.update { it.copy(idInteres = id) }
    }

    fun setBusqueda(texto: String) {
        _filtros.update { it.copy(textoBusqueda = texto) }
    }

    fun setFecha(fecha: LocalDate?) {
        _filtros.update { it.copy(fecha = fecha) }
    }

    fun setDistancia(distancia: Float) {
        _filtros.update { it.copy(distanciaMax = distancia) }
    }

}