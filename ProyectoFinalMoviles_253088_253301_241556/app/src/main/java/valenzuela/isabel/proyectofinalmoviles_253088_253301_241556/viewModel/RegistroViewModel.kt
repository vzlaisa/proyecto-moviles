package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import java.time.LocalDate

class RegistroViewModel(private val repository: UsuarioRepository): ViewModel() {

    var correo by mutableStateOf("")
        private set
    var pass by mutableStateOf("")
        private set

    var confirmarPass by mutableStateOf("")
        private set

    var mostrarErrorPass by mutableStateOf(false)
        private set

    val passCoinciden : Boolean
        get() = pass == confirmarPass && confirmarPass.isNotEmpty()

    val reglas: List<Pair<String, Boolean>>
        get() = listOf(
            "Al menos 8 caracteres" to (pass.length >= 8),
            "Al menos una mayúscula" to pass.any { it.isUpperCase() },
            "Al menos un número" to pass.any { it.isDigit() },
        )

    val formularioPaso1Valido: Boolean
        get() = reglas.all { it.second } && correoValido && passCoinciden

    val correoValido: Boolean
        get() = correo.isNotBlank()

    var nombre by mutableStateOf("")
        private set

    var apellidoPaterno by mutableStateOf("")
        private set

    var apellidoMaterno by mutableStateOf<String?>(null)  // opcional
        private set

    var ocupacion by mutableStateOf("")
        private set

    var genero by mutableStateOf<Genero?>(null)
        private set

    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set

    val generosOpciones = Genero.entries.toList()

    val formularioPaso2Valido: Boolean
        get() = nombre.isNotBlank() &&
                apellidoPaterno.isNotBlank() &&
                ocupacion.isNotBlank() &&
                genero != null &&
                fechaNacimiento != null

    var nickname by mutableStateOf("")
        private set

    var fotoUri by mutableStateOf<Uri?>(null)
        private set

    val formularioPaso3Valido: Boolean
        get() = nickname.isNotBlank()

    var interesesSeleccionados by mutableStateOf<Set<Interes>>(emptySet())
        private set

    val formularioPaso4Valido: Boolean
        get() = interesesSeleccionados.isNotEmpty()

    fun onCorreoChange(value: String) {
        correo = value
    }

    fun onPassChange(value: String) {
        pass = value
    }

    fun onConfirmarPassChange(value: String) {
        confirmarPass = value
        mostrarErrorPass = false
    }

    fun onConfirmarPassFocusLost() {
        if (confirmarPass.isNotEmpty()) {
            mostrarErrorPass = !passCoinciden
        }
    }

    fun onSiguienteClickPaso1(): Boolean {
        mostrarErrorPass = !passCoinciden
        return formularioPaso1Valido
    }

    fun onNombreChange(value: String) {
        nombre = value
    }

    fun onApellidoPaternoChange(value: String) {
        apellidoPaterno = value

    }
    fun onApellidoMaternoChange(value: String) {
        apellidoMaterno = value
    }

    fun onOcupacionChange(value: String) {
        ocupacion = value
    }

    fun onGeneroChange(value: Genero) {
        genero = value
    }

    fun onFechaNacimientoChange(value: LocalDate) {
        fechaNacimiento = value
    }

    fun onSiguienteClickPaso2(): Boolean = formularioPaso2Valido

    fun onNicknameChange(value: String) {
        nickname = value
    }
    fun onFotoChange(uri: Uri) {
        fotoUri = uri
    }

    fun onSiguienteClickPaso3(): Boolean = formularioPaso3Valido

    fun onInteresToggle(interes: Interes) {
        interesesSeleccionados = if (interes in interesesSeleccionados) {
            interesesSeleccionados - interes
        } else {
            interesesSeleccionados + interes
        }
    }

    fun onFinishClickPaso4(): Boolean = formularioPaso4Valido

    fun registrarUsuario() {
        val usuario = UsuarioEntity(
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            nickname = nickname,
            correo = correo,
            contrasenia = pass,
            genero = genero!!,
            ocupacion = ocupacion,
            fechaNacimiento = fechaNacimiento!!,
            fotoPerfil = fotoUri?.toString()
        )

        viewModelScope.launch {
            repository.registrar(usuario, interesesSeleccionados.toList())
        }
    }
}