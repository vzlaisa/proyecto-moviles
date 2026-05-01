package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
    }

    val isFirstTimeFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_FIRST_TIME] ?: true }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_LOGGED_IN] ?: false }

    val nicknameInFlow: Flow<String> = context.dataStore.data
        .map { it[NICKNAME] ?: "" }

    val fingerprintAllowedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[FINGERPRINT_ALLOWED] ?: false}

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it[NICKNAME] = ""
            it[FINGERPRINT_ALLOWED] = false
        }
    }

    suspend fun saveSession(nickname: String, fingerprint: Boolean) {
        context.dataStore.edit {
            it[NICKNAME] = nickname
            it[IS_LOGGED_IN] = true
            it[IS_FIRST_TIME] = false
            it[FINGERPRINT_ALLOWED] = fingerprint
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
}
