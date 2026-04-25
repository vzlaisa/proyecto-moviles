package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation.AppNavigation
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel(DataStoreManager(this))
        val registroViewModel = RegistroViewModel(UsuarioRepository(AppDatabase.getDatabase(this).usuarioDao()))
        setContent {
            ProyectoFinalMoviles_253088_253301_241556Theme {
                AppNavigation(authViewModel, registroViewModel)
            }
        }
    }
}
