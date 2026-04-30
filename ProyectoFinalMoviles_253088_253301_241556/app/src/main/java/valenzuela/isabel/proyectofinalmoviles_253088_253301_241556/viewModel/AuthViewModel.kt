package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository

class AuthViewModel(private val dataStore : DataStoreManager, private val repository: UsuarioRepository): ViewModel() {

    val isFirstTime = dataStore.isFirstTimeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val isLoggedIn = dataStore.isLoggedInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val username = dataStore.usernameFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    var loginError by mutableStateOf<String?>(null)
        private set

    var correoError by mutableStateOf<String?>(null)
        private set

    var passError by mutableStateOf<String?>(null)
        private set

    fun login(correo: String, pass: String) {
        loginError = null

        correoError = if (correo.isBlank()) "El correo es requerido" else null
        passError = if (pass.isBlank()) "La contraseña es requerida" else null

        if (correoError != null || passError != null) {
            return
        }

        viewModelScope.launch {
            try {
                val usuario = repository.login(correo, pass)

                if (usuario != null) {
                    dataStore.saveSession(usuario.usuario.nickname)
                } else {
                    loginError = "Correo o contraseña incorrectos"
                }
            } catch(e: Exception) {
                loginError = "Ocurrió un error al iniciar sesión. Intenta de nuevo."
                Log.e("LOGIN", "Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.logout()
        }
    }

    fun setFirstTime(value: Boolean) {
        viewModelScope.launch {
            dataStore.setFirstTime(value)
        }
    }
}