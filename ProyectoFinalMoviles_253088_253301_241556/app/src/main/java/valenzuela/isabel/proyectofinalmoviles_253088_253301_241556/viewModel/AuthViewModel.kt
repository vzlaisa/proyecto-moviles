package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository

class AuthViewModel(private val dataStore : DataStoreManager, private val repository: UsuarioRepository): ViewModel() {

    init {
        viewModelScope.launch {
            // Esperar a que el flow del DataStore emita al menos un valor
            dataStore.nicknameInFlow.collect {
                // En cuanto el DataStore responde (aunque sea con un string vacío), ya terminó la verificación inicial
                _isCheckingAccount.value = false
            }
        }
    }

    private val _isCheckingAccount = MutableStateFlow(true)
    val isCheckingAccount = _isCheckingAccount.asStateFlow()

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

    val isFingerprintAllowed = dataStore.fingerprintAllowedInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val nickname = dataStore.nicknameInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val fotoPerfil = dataStore.nicknameInFlow.flatMapLatest { nickname ->
        repository.getFotoPerfil(nickname)

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val hasAccount = dataStore.nicknameInFlow.map { it.isNotBlank() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    var loginError by mutableStateOf<String?>(null)
        private set

    var correoError by mutableStateOf<String?>(null)
        private set

    var passError by mutableStateOf<String?>(null)
        private set

    fun login(correo: String, pass: String) {
        loginError = null

        correoError = if (!hasAccount.value && correo.isBlank()) {
            "El correo es requerido"
        } else null

        passError = if (pass.isBlank()) "La contraseña es requerida" else null

        if (correoError != null || passError != null) {
            return
        }

        viewModelScope.launch {
            try {
                val identificador = if (hasAccount.value) nickname.value else correo

                val usuario = repository.login(identificador, pass)

                if (usuario != null) {
                    dataStore.saveSession(
                        usuario.usuario.nickname,
                        usuario.usuario.huellaActiva
                    )
                } else {
                    loginError = "Correo o contraseña incorrectos"
                }
            } catch(e: Exception) {
                loginError = "Ocurrió un error al iniciar sesión. Intenta de nuevo."
                Log.e("LOGIN", "Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun loginConHuella() {
        viewModelScope.launch {
            val usuario = repository.getByIdentificador(nickname.value)
            if (usuario != null) {
                dataStore.saveSession(
                    usuario.usuario.nickname,
                    usuario.usuario.huellaActiva
                )
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

    fun clearSession() {
        viewModelScope.launch {
            dataStore.clearSession()
        }
    }
}