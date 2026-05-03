package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository

class ConfigViewModel(private val dataStore: DataStoreManager, private val repository: UsuarioRepository): ViewModel() {

    val biometricoEnabled = dataStore.fingerprintAllowedInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun onBiometricoChanged(value: Boolean) {
        viewModelScope.launch {
            try {
                // Actualizar en la base
                actualizarHuellaActiva(value)

                // Cambiar en el data store
                dataStore.setFingerprintAllowed(value)
            } catch (e: Exception) {
                Log.e("ACTUALIZAR_HUELLA_ACTIVA", "Error al actualizar huella: ${e.message}")
            }
        }
    }

    private suspend fun actualizarHuellaActiva(value: Boolean) {
        val nickname = dataStore.nicknameInFlow.first()

        if (nickname.isNotBlank()) {
            repository.actualizarHuellaActiva(nickname, value)
        } else {
            throw IllegalStateException("No hay usuario logueado")
        }
    }

}