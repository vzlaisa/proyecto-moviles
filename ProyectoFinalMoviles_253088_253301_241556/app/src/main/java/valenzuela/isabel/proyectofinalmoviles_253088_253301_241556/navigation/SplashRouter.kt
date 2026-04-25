package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SplashRouter(
    isFirstTime: Boolean?,
    isLoggedIn: Boolean?,
    navController: NavController
) {

    LaunchedEffect(isFirstTime, isLoggedIn) {

        if (isFirstTime == null || isLoggedIn == null) return@LaunchedEffect

        val destination = when {
            isFirstTime -> Screen.MainScreen.route
            isLoggedIn -> Screen.Home.route
            else -> Screen.Login.route
        }

        navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}