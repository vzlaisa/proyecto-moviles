package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "session_prefs")

    companion object {
        val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val NICKNAME = stringPreferencesKey("nickname")
        val FINGERPRINT_ALLOWED = booleanPreferencesKey("fingerprint_allowed")
        val USUARIO_ID = intPreferencesKey("usuario_id")
        val IS_NEW_ACCOUNT = booleanPreferencesKey("is_new_account")
    }

    val isFirstTimeFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_FIRST_TIME] ?: true }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_LOGGED_IN] ?: false }

    val nicknameInFlow: Flow<String> = context.dataStore.data
        .map { it[NICKNAME] ?: "" }

    val fingerprintAllowedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[FINGERPRINT_ALLOWED] ?: false}

    val usuarioIdFlow: Flow<Int> = context.dataStore.data
        .map { it[USUARIO_ID] ?: -1 }

    val isNewAccountInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_NEW_ACCOUNT] ?: true }

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it[NICKNAME] = ""
            it[FINGERPRINT_ALLOWED] = false
            it[USUARIO_ID] = -1
        }
    }

    suspend fun saveSession(nickname: String, fingerprint: Boolean, usuarioId: Int) {
        context.dataStore.edit {
            it[NICKNAME] = nickname
            it[IS_LOGGED_IN] = true
            it[FINGERPRINT_ALLOWED] = fingerprint
            it[USUARIO_ID] = usuarioId
        }
    }

    suspend fun setFirstTime(value: Boolean) {
        context.dataStore.edit {
            it[IS_FIRST_TIME] = value
        }
    }

    suspend fun setFingerprintAllowed(value: Boolean) {
        context.dataStore.edit {
            it[FINGERPRINT_ALLOWED] = value
        }
    }

    suspend fun setIsNewAccount(value: Boolean) {
        context.dataStore.edit {
            it[IS_NEW_ACCOUNT] = value
        }
    }
}
