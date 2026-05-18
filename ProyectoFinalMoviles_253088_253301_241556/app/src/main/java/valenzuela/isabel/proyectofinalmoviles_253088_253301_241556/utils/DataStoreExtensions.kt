package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(
    name = "session_prefs"
)