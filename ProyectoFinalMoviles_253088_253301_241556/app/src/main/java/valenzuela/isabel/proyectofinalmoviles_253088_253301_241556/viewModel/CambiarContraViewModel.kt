package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.SecurityUtils

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

    var actualizacionExitosa by mutableStateOf(false)
        private set

    fun onCorreoChange(value: String) {
        correoError = null
        mensajeError = null
        usuarioEncontrado = false
        correo = value
    }

    fun onNuevaPassChange(value: String) {
        mensajeError = null
        nuevaPass = value
    }

    fun onConfirmarNuevaPassChange(value: String) {
        mensajeError = null
        confirmarNuevaPass = value
    }

    fun verificarCorreo() {
        mensajeError = null
        usuarioEncontrado = false

        if (!correoValido) {
            correoError = "El correo no es válido"
            return
        }

        viewModelScope.launch {
            try {
                val usuario = repository.getByIdentificador(correo)
                if (usuario == null) {
                    mensajeError = "No se ha encontrado una cuenta asociada a este correo"
                } else {
                    usuarioEncontrado = true
                }
            } catch (e: Exception) {
                mensajeError = "Ocurrió un error al verificar el correo. Intenta de nuevo."
                Log.e("VERIFICAR_CORREO", "Error: ${e.message}")
            }
        }
    }

    fun cambiarContrasenia() {
        mensajeError = null

        if (!reglas.all { it.second }) {
            return
        }

        viewModelScope.launch {
            try {
                repository.actualizarContrasenia(correo, nuevaPass)

                actualizacionExitosa = true
            } catch (e: ValidationException) {
                mensajeError = e.message
            }
            catch (e: Exception) {
                mensajeError = "Ocurrió un error al actualizar la contraseña. Intenta de nuevo."
                Log.e("ACTUALIZAR_CONTRASEÑA", "Error: ${e.message}")
            }
        }
    }

    fun resetNavegacion() {
        usuarioEncontrado = false
    }

    fun reset() {
        correo = ""
        usuarioEncontrado = false
        actualizacionExitosa = false
        nuevaPass = ""
        confirmarNuevaPass = ""

        correoError = null
        mensajeError = null
        nuevaPassError = null
        confirmarNuevaPassError = null
    }
}