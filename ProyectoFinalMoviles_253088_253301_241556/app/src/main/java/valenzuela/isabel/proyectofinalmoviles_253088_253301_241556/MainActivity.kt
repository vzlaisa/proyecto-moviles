package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556

import android.Manifest
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.SyncManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.ActividadRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.InscripcionRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.NotificacionRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation.AppNavigation
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.ConfigViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CrearActividadViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.DetalleActividadViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.EditarActividadViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.HomeViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.NotificacionViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.PerfilViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

class MainActivity : FragmentActivity() {
    private lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TOKEN_FIREBASE", task.result)
                } else {
                    Log.e("TOKEN_FIREBASE", "Error", task.exception)
                }
            }

        enableEdgeToEdge()

        // Inicialización de dependencias
        val database by lazy { AppDatabase.getDatabase(this) }
        val usuarioRepo by lazy { UsuarioRepository(database.usuarioDao()) }
        val actividadRepo by lazy { ActividadRepository(database.actividadDao()) }
        val inscripcionRepo by lazy { InscripcionRepository(database.inscripcionDao(), database.actividadDao()) }
        val notificacionRepo by lazy { NotificacionRepository(database.notificacionDao()) }
        val dataStore by lazy { DataStoreManager(this) }

        syncManager = SyncManager(
            actividadDAO = database.actividadDao(),
            usuarioDAO = database.usuarioDao(),
            inscripcionDAO = database.inscripcionDao()
        )
        syncManager.iniciar()

        // Factory única para todos los ViewModels
        val factory = JoinlyViewModelFactory(usuarioRepo, actividadRepo, inscripcionRepo, notificacionRepo, dataStore, this.application)

        // Delegados de ViewModels
        val authViewModel: AuthViewModel by viewModels { factory }
        val registroViewModel: RegistroViewModel by viewModels { factory }
        val cambiarContraViewModel: CambiarContraViewModel by viewModels { factory }
        val homeViewModel: HomeViewModel by viewModels { factory }
        val perfilViewModel: PerfilViewModel by viewModels { factory }
        val configViewModel: ConfigViewModel by viewModels { factory }
        val crearActividadViewModel: CrearActividadViewModel  by viewModels { factory }
        val detalleActividadViewModel: DetalleActividadViewModel by viewModels { factory }
        val editarActividadViewModel: EditarActividadViewModel by viewModels { factory }

        setContent {
            ProyectoFinalMoviles_253088_253301_241556Theme {
                AppNavigation(
                    authViewModel = authViewModel,
                    registroViewModel = registroViewModel,
                    cambiarContraViewModel = cambiarContraViewModel,
                    homeViewModel = homeViewModel,
                    perfilViewModel = perfilViewModel,
                    configViewModel = configViewModel,
                    crearActividadViewModel = crearActividadViewModel,
                    detalleActividadViewModel = detalleActividadViewModel,
                    editarActividadViewModel = editarActividadViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncManager.detener()
    }
}

private class JoinlyViewModelFactory(
    private val usuarioRepo: UsuarioRepository,
    private val actividadRepo: ActividadRepository,
    private val inscripcionRepo: InscripcionRepository,
    private val notificacionRepo: NotificacionRepository,
    private val dataStore: DataStoreManager,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(dataStore, usuarioRepo) as T
            modelClass.isAssignableFrom(RegistroViewModel::class.java) ->
                RegistroViewModel(usuarioRepo) as T
            modelClass.isAssignableFrom(CambiarContraViewModel::class.java) ->
                CambiarContraViewModel(usuarioRepo) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository = actividadRepo, dataStore = dataStore, application = application) as T
            modelClass.isAssignableFrom(PerfilViewModel::class.java) ->
                PerfilViewModel(dataStore, usuarioRepo) as T
            modelClass.isAssignableFrom(ConfigViewModel::class.java) ->
                ConfigViewModel(dataStore, usuarioRepo) as T
            modelClass.isAssignableFrom(CrearActividadViewModel::class.java) ->
                CrearActividadViewModel(dataStore, actividadRepo) as T
            modelClass.isAssignableFrom(EditarActividadViewModel::class.java) ->
                EditarActividadViewModel(actividadRepo) as T
            modelClass.isAssignableFrom(DetalleActividadViewModel::class.java) ->
                DetalleActividadViewModel(actividadRepo, inscripcionRepo, dataStore) as T
            modelClass.isAssignableFrom(NotificacionViewModel::class.java) ->
                NotificacionViewModel(dataStore, notificacionRepo) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
