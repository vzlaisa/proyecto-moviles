package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.FiltrosActividades
import java.time.LocalDate

class HomeViewModel(
    private val actividadDAO: ActividadDAO
): ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosActividades())
    val filtros = _filtros.asStateFlow()

    val actividades = _filtros
        .flatMapLatest { filtros ->
            actividadDAO.getActividadesFiltradas(
                idInteres = filtros.idInteres,
                texto = filtros.textoBusqueda,
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