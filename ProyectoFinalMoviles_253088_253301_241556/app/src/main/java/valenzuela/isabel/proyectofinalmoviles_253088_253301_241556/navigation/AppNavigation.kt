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
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    val isFirstTime by authViewModel.isFirstTime.collectAsState(null)
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(null)
    val username by authViewModel.username.collectAsState()

    val startDestination = when {
        isFirstTime == true -> Screen.MainScreen.route
        isLoggedIn == true -> Screen.Home.route
        else -> Screen.Login.route
    }

    if (isFirstTime == null || isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
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
        composable(Screen.Login.route) {
            LoginScreen()
        }

        // Registrarse

        // Home
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            navController.navigate(Screen.Home.route) {
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