package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager

class AuthViewModel(private val dataStore : DataStoreManager): ViewModel() {

    val isFirstTime = dataStore.isFirstTimeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val isLoggedIn = dataStore.isLoggedInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val username = dataStore.usernameFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    fun login(user: String, pass: String) {
        if (user == "admin" && pass == "1234") {
            viewModelScope.launch {
                dataStore.saveSession(user)
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