package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.HomeScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.LoginScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.MainScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso1
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso2
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso3
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.RegistroPaso4
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    registroViewModel: RegistroViewModel
) {
    val navController = rememberNavController()

    val isFirstTime by authViewModel.isFirstTime.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val username by authViewModel.username.collectAsState()

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
                onCambiarContra = {},
                onLogin = {},
                onRegistrarse = {
                    navController.navigate(Screen.SignUp.route)
                }
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
}