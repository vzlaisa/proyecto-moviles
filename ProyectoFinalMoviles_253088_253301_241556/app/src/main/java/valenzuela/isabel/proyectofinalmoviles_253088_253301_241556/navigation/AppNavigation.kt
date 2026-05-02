package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.ActualizarContraScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.CambiarContraScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.HomeScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.LoginScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.MainScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso1
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso2
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso3
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso4
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    registroViewModel: RegistroViewModel,
    cambiarContraViewModel: CambiarContraViewModel
) {
    val navController = rememberNavController()

    val isFirstTime by authViewModel.isFirstTime.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Esta bandera evita que el efecto global choque con el splash al arrancar
    var yaPasoElSplash by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        // Si está arrancando no hace nada y deja que el splash decida
        if (!yaPasoElSplash) {
            if (isLoggedIn != null) yaPasoElSplash = true
            return@LaunchedEffect
        }

        // Esto solo corre si el usuario hace login o logout manualmente
        if (isLoggedIn == true) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (isLoggedIn == false) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
                cambiarContraViewModel
            )
        }

        // Home
        composable(Screen.Home.route) {
            authViewModel.setFirstTime(false)
            HomeScreen()
        }
    }

}

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object Login: Screen("login")
    object SignUp: Screen("sign_up")
    object Home: Screen("home")
    object CambiarContra: Screen("cambiar_contrasenia")
    object ActualizarContra: Screen("actualizar_contrasenia")
}