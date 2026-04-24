package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens.MainScreen
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController();

    val isFirstTime by authViewModel.isFirstTime.collectAsState();
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState();
    val username by authViewModel.username.collectAsState();

    val startDestination = when {
        isFirstTime -> Screen.MainScreen.route
        isLoggedIn -> Screen.Home.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Main screen
        composable(Screen.MainScreen.route) {
            MainScreen(
                onLoginClick = {
                    authViewModel.setFirstTime(false)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainScreen.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    authViewModel.setFirstTime(false)
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.MainScreen.route) { inclusive = true }
                    }
                }
            )
        }

        // Login

        // Registrarse

        // Home
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) // limpia toda la pila
                launchSingleTop = true // para que no duplique pantallas
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

}

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object Login: Screen("login")
    object SignUp: Screen("sign_up")
    object Home: Screen("home")
}