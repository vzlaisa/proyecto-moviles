package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation.AppNavigation
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel(DataStoreManager(this), UsuarioRepository(AppDatabase.getDatabase(this).usuarioDao()))
        val registroViewModel = RegistroViewModel(UsuarioRepository(AppDatabase.getDatabase(this).usuarioDao()))
        val cambiarContraViewModel = CambiarContraViewModel(UsuarioRepository(AppDatabase.getDatabase(this).usuarioDao()))
        setContent {
            ProyectoFinalMoviles_253088_253301_241556Theme {
                AppNavigation(authViewModel, registroViewModel, cambiarContraViewModel)
            }
        }
    }
}
