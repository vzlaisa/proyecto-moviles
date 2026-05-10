package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.FiltrosActividades
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.LocationHelper
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel(
    private val repository: ActividadRepository,
    private val dataStore: DataStoreManager,
    application: Application
): AndroidViewModel(application) {

    private val _filtros = MutableStateFlow(FiltrosActividades())
    val filtros = _filtros.asStateFlow()

    private val _ubicacionUsuario = MutableStateFlow<Pair<Double, Double>?>(null)

    // Obtiene el nickname del usuario con sesión iniciada
    val nickname = dataStore.nicknameInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

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

    val actividades = combine(_filtros, _ubicacionUsuario) { filtros, ubicacion ->
        filtros.copy(
            latUsuario = ubicacion?.first,
            lonUsuario = ubicacion?.second
        )
    }.flatMapLatest { filtrosConUbicacion ->
        repository.getActividadesFiltradas(
            idInteres = filtrosConUbicacion.idInteres,
            busqueda = filtrosConUbicacion.textoBusqueda,
            fecha = filtrosConUbicacion.fecha
        ).map { lista ->
            filtrarPorDistancia(lista, filtrosConUbicacion)
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
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

    fun cargarUbicacion(context: Context) {
        viewModelScope.launch {
            val ubicacion = LocationHelper.getUbicacion(context.applicationContext)
            _ubicacionUsuario.value = ubicacion
        }
    }

    private fun filtrarPorDistancia(
        lista: List<ActividadConDetalle>,
        filtros: FiltrosActividades
    ): List<ActividadConDetalle> {
        val lat = filtros.latUsuario ?: return lista
        val lon = filtros.lonUsuario ?: return lista
        val maxKm = filtros.distanciaMax ?: return lista
        Log.d("DISTANCIA", "Usuario: ($lat, $lon) | Filtro: ${maxKm}km")

        return lista.filter { item ->
            val distancia = LocationHelper.calcularDistanciaKm(
                lat, lon,
                item.actividad.latitud,
                item.actividad.longitud
            )
            Log.d("DISTANCIA", "${item.actividad.nombre}: lat=${item.actividad.latitud}, lon=${item.actividad.longitud} → ${distancia}km")
            distancia <= maxKm
        }
    }

    fun limpiar() {
        _filtros.value = FiltrosActividades()
        _ubicacionUsuario.value = null
    }

    fun eliminarActividad(actividad: ActividadEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.eliminarActividad(actividad)
        }
    }

}