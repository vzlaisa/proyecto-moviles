package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses

class PerfilViewModel(
    private val dataStore: DataStoreManager,
    private val repository: UsuarioRepository
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val usuario = combine(dataStore.nicknameInFlow, refreshTrigger) { nickname, _ -> nickname }
        .flatMapLatest { nickname ->
            if (nickname.isNullOrBlank()) {
                flow<UsuarioConIntereses?> { emit(null) }
            } else {
                flow<UsuarioConIntereses?> { emit(repository.getByIdentificador(nickname)) } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var cantidadActividadesCreadas by mutableIntStateOf(0)
        private set

    var cantidadActividadesUnidas by mutableIntStateOf(0)
        private set

    var enModoEdicion by mutableStateOf(false)
        private set
    var nombreEdit by mutableStateOf("")
    var apellidoPaternoEdit by mutableStateOf("")
    var apellidoMaternoEdit by mutableStateOf("")
    var ocupacionEdit by mutableStateOf("")
    var generoEdit by mutableStateOf<Genero?>(null)
    var fechaNacimientoEdit by mutableStateOf<LocalDate?>(null)
    var emailEdit by mutableStateOf("")
    var passwordEdit by mutableStateOf("")
    var fotoPerfilEdit by mutableStateOf<String?>(null)
    var fotoPerfilBitmap by mutableStateOf<Bitmap?>(null)
    var interesesSeleccionados by mutableStateOf<List<Interes>>(emptyList())
        private set
    var actualizacionExitosa by mutableStateOf(false)
    var actualizacionError by mutableStateOf<String?>(null)
    var confirmacionPasswordEdit by mutableStateOf("")

    fun cargarEstadisticas() {
        val id = usuario.value?.usuario?.id?: return

            viewModelScope.launch {
                try {
                    val actividadesCreadas = repository.obtenerCantidadActividadesCreadas(id)
                    val actividadesUnidas = repository.obtenerCantidadActividadesUnidas(id)

                    cantidadActividadesCreadas = actividadesCreadas.coerceAtLeast(0)
                    cantidadActividadesUnidas = actividadesUnidas.coerceAtLeast(0)
                } catch (e: Exception) {
                    Log.e("PERFIL", "Error al cargar estadísticas: ${e.message}")
                    cantidadActividadesCreadas = 0
                    cantidadActividadesUnidas = 0
                }
            }
    }

    fun activarModoEdicion() {
        val usuarioActual = usuario.value ?: return

        nombreEdit = usuarioActual.usuario.nombre
        apellidoPaternoEdit = usuarioActual.usuario.apellidoPaterno
        apellidoMaternoEdit = usuarioActual.usuario.apellidoMaterno ?: ""
        ocupacionEdit = usuarioActual.usuario.ocupacion
        interesesSeleccionados = usuarioActual.intereses.map { entidad ->
            entidad.nombre
        }

        generoEdit = usuarioActual.usuario.genero
        fechaNacimientoEdit = usuarioActual.usuario.fechaNacimiento
        emailEdit = usuarioActual.usuario.correo
        fotoPerfilEdit = usuarioActual.usuario.fotoPerfil
        passwordEdit = ""
        confirmacionPasswordEdit = ""

        enModoEdicion = true
    }

    fun cancelarEdicion() {
        enModoEdicion = false
        actualizacionError = null
    }

    fun toggleInteres(interes: Interes) {
        interesesSeleccionados = if (interesesSeleccionados.contains(interes)) {
            interesesSeleccionados - interes
        } else {
            interesesSeleccionados + interes
        }
    }

    fun guardarCambios(filesDir: File) {
        val datosActuales = usuario.value?.usuario ?: return

        if (passwordEdit.isNotBlank() && passwordEdit != confirmacionPasswordEdit) {
            actualizacionError = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            try {
                var rutaFinalImagen = fotoPerfilEdit
                fotoPerfilBitmap?.let { bitmap ->
                    rutaFinalImagen = repository.guardarImagenNueva(
                        bitmap,
                        datosActuales.nickname,
                        filesDir
                    )
                }
                val usuarioActualizado = datosActuales.copy(
                    nombre = nombreEdit,
                    apellidoPaterno = apellidoPaternoEdit,
                    apellidoMaterno = apellidoMaternoEdit.takeIf { it.isNotBlank() },
                    ocupacion = ocupacionEdit,
                    genero = generoEdit ?: datosActuales.genero,
                    fechaNacimiento = fechaNacimientoEdit ?: datosActuales.fechaNacimiento,
                    fotoPerfil = rutaFinalImagen,
                    correo = emailEdit,
                    contrasenia = if (passwordEdit.isNotBlank()) passwordEdit else datosActuales.contrasenia
                )

                repository.actualizarPerfil(usuarioActualizado, interesesSeleccionados)

                fotoPerfilBitmap = null
                refreshTrigger.value += 1
                actualizacionExitosa = true
                enModoEdicion = false
            } catch (e: Exception) {
                actualizacionExitosa = false
                actualizacionError = e.message
            }
        }
    }
}