package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation.AppNavigation
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.HomeViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialización de dependencias
        val database by lazy { AppDatabase.getDatabase(this) }
        val usuarioRepo by lazy { UsuarioRepository(database.usuarioDao()) }
        val actividadRepo by lazy { ActividadRepository(database.actividadDao()) }
        val dataStore by lazy { DataStoreManager(this) }

        // Factory única para todos los ViewModels
        val factory = JoinlyViewModelFactory(usuarioRepo, actividadRepo, dataStore)

        // Delegados de ViewModels
        val authViewModel: AuthViewModel by viewModels { factory }
        val registroViewModel: RegistroViewModel by viewModels { factory }
        val cambiarContraViewModel: CambiarContraViewModel by viewModels { factory }
        val homeViewModel: HomeViewModel by viewModels { factory }

        setContent {
            ProyectoFinalMoviles_253088_253301_241556Theme {
                AppNavigation(
                    authViewModel = authViewModel,
                    registroViewModel = registroViewModel,
                    cambiarContraViewModel = cambiarContraViewModel,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}

private class JoinlyViewModelFactory(
    private val usuarioRepo: UsuarioRepository,
    private val actividadRepo: ActividadRepository,
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(dataStore, usuarioRepo) as T
            modelClass.isAssignableFrom(RegistroViewModel::class.java) ->
                RegistroViewModel(usuarioRepo) as T
            modelClass.isAssignableFrom(CambiarContraViewModel::class.java) ->
                CambiarContraViewModel(usuarioRepo) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(actividadRepo) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
