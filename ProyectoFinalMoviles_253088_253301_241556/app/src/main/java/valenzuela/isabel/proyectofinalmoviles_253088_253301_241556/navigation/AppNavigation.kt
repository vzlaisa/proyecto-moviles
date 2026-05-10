package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.ActualizarContraScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.CambiarContraScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.ConfiguracionScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.CrearActividadPaso1
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.CrearActividadPaso2
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.CrearActividadPaso3
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.DetalleActividadScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.HomeScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.LoginScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.MainScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.PerfilScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso1
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso2
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso3
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso4
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.HomeViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.PerfilViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.EditarPerfilScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.NotificacionesScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.ConfigViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CrearActividadViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.EditarActividadViewModel


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    registroViewModel: RegistroViewModel,
    cambiarContraViewModel: CambiarContraViewModel,
    homeViewModel: HomeViewModel,
    perfilViewModel: PerfilViewModel,
    configViewModel: ConfigViewModel,
    crearActividadViewModel: CrearActividadViewModel,
    editarActividadViewModel: EditarActividadViewModel
) {
    val navController = rememberNavController()

    // Rastreo de la ruta actual para saber si mostrar la barra
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rutasConNavigationBar = listOf(
        Screen.Home.route,
        Screen.Perfil.route,
    )

    val isFirstTime by authViewModel.isFirstTime.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Esta bandera evita que el efecto global choque con el splash al arrancar
    var yaPasoElSplash by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        // Si está arrancando no hace nada y deja que el splash decida
        if (!yaPasoElSplash) {
            if (isLoggedIn != null) {
                yaPasoElSplash = true // Ta terminó la carga inicial
            }
            // No navegar para no pelear con el splash
            return@LaunchedEffect
        }

        // Esto solo corre si el usuario hace login o logout manualmente
        if (isLoggedIn == true) {
            navController.navigate(Screen.Home.route) {
                homeViewModel.limpiar()
                popUpTo(0) { inclusive = true }
            }
        } else if (isLoggedIn == false) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Solo se muestra si la ruta actual está en la lista
            if (currentRoute in rutasConNavigationBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    onLogoutClick = { authViewModel.logout() }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashRouter(
                        isFirstTime = isFirstTime,
                        isLoggedIn = isLoggedIn,
                        navController = navController
                    )
                }

                // Pantallas de Editar Actividad
                composable(
                    route = Screen.EditarActividadPaso1.route,
                    arguments = listOf(navArgument("actividadId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("actividadId") ?: 0

                    LaunchedEffect(id) { editarActividadViewModel.cargarActividad(id) }

                    valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.EditarActividadPaso1(
                        viewModel = editarActividadViewModel,
                        onClose = {
                            editarActividadViewModel.limpiarDatos()
                            navController.popBackStack()
                        },
                        onNext = { navController.navigate(Screen.EditarPaso2.route) }
                    )
                }

                composable(Screen.EditarPaso2.route) {
                    valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.EditarActividadPaso2(
                        viewModel = editarActividadViewModel,
                        onClose = {
                            editarActividadViewModel.limpiarDatos()
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onNext = { navController.navigate(Screen.EditarPaso3.route) }
                    )
                }

                composable(Screen.EditarPaso3.route) {
                    valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.EditarActividadPaso3(
                        viewModel = editarActividadViewModel,
                        onClose = {
                            editarActividadViewModel.limpiarDatos()
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onGuardar = { navController.popBackStack(Screen.Home.route, inclusive = false) }
                    )
                }

                // Onboarding
                composable(Screen.MainScreen.route) {
                    MainScreen(
                        onLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        },
                        onSignUpClick = {
                            navController.navigate(Screen.SignUp.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }

                // Login
                composable(Screen.Login.route) {
                    LoginScreen(
                        onCambiarCuenta = {
                            authViewModel.clearSession()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        },
                        onCambiarContra = {
                            navController.navigate(Screen.CambiarContra.route)
                        },
                        onRegistrarse = {
                            navController.navigate(Screen.SignUp.route)
                        },
                        authViewModel
                    )
                }

                // Wizard de registrarse
                composable(Screen.SignUp.route) {
                    RegistroPaso1(
                        onNext = { navController.navigate("signup_step2") },
                        registroViewModel
                    )
                }

                composable("signup_step2") {
                    RegistroPaso2(
                        onNext = { navController.navigate("signup_step3") },
                        onBack = { navController.popBackStack() },
                        registroViewModel
                    )
                }

                composable("signup_step3") {
                    RegistroPaso3(
                        onNext = { navController.navigate("signup_step4") },
                        onBack = { navController.popBackStack() },
                        registroViewModel
                    )
                }

                composable("signup_step4") {
                    RegistroPaso4(
                        onBack = { navController.popBackStack() },
                        onRegistrarseSuccess = {
                            authViewModel.setFirstTime(false)
                            registroViewModel.resetFormulario()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        },
                        registroViewModel
                    )
                }

                // Cambiar contraseña
                composable(Screen.CambiarContra.route) {
                    CambiarContraScreen(
                        onBack = { navController.popBackStack() },
                        onVerificarCorreoSuccess = { navController.navigate(Screen.ActualizarContra.route) },
                        cambiarContraViewModel
                    )
                }

                composable(Screen.ActualizarContra.route) {
                    ActualizarContraScreen(
                        onActualizarSuccess = {
                            cambiarContraViewModel.reset()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        cambiarContraViewModel
                    )
                }

                // Home
                composable(Screen.Home.route) {
                    authViewModel.setFirstTime(false)
                    HomeScreen(
                        viewModel = homeViewModel,
                        authViewModel = authViewModel,
                        configViewModel = configViewModel,
                        onClickActividad = { actividad ->
                            navController.navigate(Screen.DetalleActividad.crearRuta(actividad.actividad.id))
                        },
                        onNotificacionClick = { navController.navigate(Screen.Notificaciones.route) }
                    )
                }

                // Perfil
                composable(Screen.Perfil.route) {
                    PerfilScreen(
                        viewModel = perfilViewModel,
                        onNavigateToEditar = {
                            navController.navigate(Screen.EditarPerfil.route)
                        }
                    )
                }

                //Editar Perfil
                composable(Screen.EditarPerfil.route) {
                    EditarPerfilScreen(
                        viewModel = perfilViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Configuración
                composable(Screen.Configuracion.route) {
                    ConfiguracionScreen(
                        onBack = { navController.popBackStack() },
                        configViewModel
                    )
                }

                // Wizard de crear actividad
                composable(Screen.NuevaActividad.route) {
                    CrearActividadPaso1(
                        viewModel = crearActividadViewModel,
                        onClose = { crearActividadViewModel.limpiarDatos()
                            navController.popBackStack()
                                  },
                        onNext = { navController.navigate(Screen.CrearPaso2.route) }
                    )
                }

                composable(Screen.CrearPaso2.route) {
                    CrearActividadPaso2(
                        viewModel = crearActividadViewModel,
                        onClose = {
                            crearActividadViewModel.limpiarDatos()
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onNext = { navController.navigate(Screen.CrearPaso3.route) }
                    )
                }

                composable(Screen.CrearPaso3.route) {
                    CrearActividadPaso3(
                        viewModel = crearActividadViewModel,
                        onClose = { crearActividadViewModel.limpiarDatos()
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onPublicar = {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        }
                    )
                }

                // Detalle de actividad
                composable(
                    route = Screen.DetalleActividad.route,
                    arguments = listOf(navArgument("actividadId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("actividadId") ?: 0

                    val actividad = homeViewModel.actividades.collectAsState().value.find {
                        it.actividad.id == id
                    }

                    actividad?.let {
                        DetalleActividadScreen(
                            actividad = it,
                            usuarioActualId = homeViewModel.usuarioActualId,
                            onRegresar = { navController.popBackStack() },
                            onEditar = { navController.navigate(Screen.EditarActividadPaso1.crearRuta(it.actividad.id))},
                            onEliminar = {
                                homeViewModel.eliminarActividad(it.actividad)
                                navController.popBackStack()
                            }
                        )
                    }
                }

                // Notificaciones
                composable(Screen.Notificaciones.route) {
                    NotificacionesScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object Login: Screen("login")
    object SignUp: Screen("sign_up")
    object Home: Screen("home")
    object EditarPerfil: Screen("editar_perfil")
    object CambiarContra: Screen("cambiar_contrasenia")
    object ActualizarContra: Screen("actualizar_contrasenia")
    object Perfil: Screen("perfil")
    object Configuracion: Screen("configuracion")
    object NuevaActividad: Screen("crear_actividad_paso_1")
    object CrearPaso2: Screen("crear_actividad_paso_2")
    object CrearPaso3: Screen("crear_actividad_paso_3")

    object DetalleActividad: Screen("detalle_actividad/{actividadId}") {
        fun crearRuta(id: Int) = "detalle_actividad/$id"
    }

    object Notificaciones: Screen("notificaciones")

    object EditarActividadPaso1: Screen("editar_actividad_paso_1/{actividadId}") {
        fun crearRuta(id: Int) = "editar_actividad_paso_1/$id"
    }
    object EditarPaso2: Screen("editar_actividad_paso_2")
    object EditarPaso3: Screen("editar_actividad_paso_3")
}