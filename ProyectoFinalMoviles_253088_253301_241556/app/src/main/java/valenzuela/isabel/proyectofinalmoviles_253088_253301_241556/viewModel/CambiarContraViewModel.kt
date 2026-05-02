package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository

class CambiarContraViewModel(private val repository: UsuarioRepository): ViewModel() {

    var correo by mutableStateOf("")
        private set

    var usuarioEncontrado by mutableStateOf(false)
        private set

    var nuevaPass by mutableStateOf("")
        private set

    var confirmarNuevaPass by mutableStateOf("")
        private set

    val reglas: List<Pair<String, Boolean>>
        get() = listOf(
            "Al menos 8 caracteres" to (nuevaPass.length >= 8),
            "Al menos una mayúscula" to nuevaPass.any { it.isUpperCase() },
            "Al menos un número" to nuevaPass.any { it.isDigit() },
            "Las contraseñas coinciden" to (nuevaPass == confirmarNuevaPass && confirmarNuevaPass.isNotEmpty())
        )

    var correoError by mutableStateOf<String?>(null)
        private set

    var nuevaPassError by mutableStateOf<String?>(null)
        private set
    var confirmarNuevaPassError by mutableStateOf<String?>(null)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    val correoValido: Boolean
        get() = correo.isNotBlank() && EMAIL_ADDRESS.matcher(correo).matches() && correoError == null

    fun onCorreoChange(value: String) {
        correoError = null
        mensajeError = null
        usuarioEncontrado = false
        correo = value
    }

    fun onNuevaPassChange(value: String) {
        nuevaPass = value
    }

    fun onConfirmarNuevaPassChange(value: String) {
        confirmarNuevaPass = value
    }

    fun verificarCorreo() {
        if (!correoValido) {
            correoError = "El correo no es válido"
            return
        }

        viewModelScope.launch {
            val usuario = repository.getByIdentificador(correo)
            if (usuario == null) {
                mensajeError = "No se ha encontrado una cuenta asociada a este correo"
                usuarioEncontrado = false
            } else {
                usuarioEncontrado = true
                mensajeError = null
            }
        }
    }

    fun cambiarContrasenia() {
    }

    fun resetNavegacion() {
        usuarioEncontrado = false
    }

    fun reset() {
        correo = ""
        usuarioEncontrado = false
        nuevaPass = ""
        confirmarNuevaPass = ""

        correoError = null
        mensajeError = null
    }
}