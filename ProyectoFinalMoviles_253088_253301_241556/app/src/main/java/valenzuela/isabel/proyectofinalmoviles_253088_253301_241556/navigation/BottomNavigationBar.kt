package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    onLogoutClick: () -> Unit
) {
    val items = listOf(
        Pair(Screen.Home, Icons.Default.Home to "Inicio"),
        Pair(Screen.Perfil, Icons.Default.Person to "Mi perfil"),
        Pair(Screen.NuevaActividad, Icons.Default.Add to "")
    )

    var showMenu by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        items.forEach { (screen, info) ->
            val (icon, label) = info

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(label) },
                icon = { Icon(icon, contentDescription = label) }
            )
        }

        // Item de más
        NavigationBarItem(
            selected = false,
            onClick = { showMenu = true },
            label = { Text("Más") },
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Más opciones"
                    )

                    // Configuración de la cuenta
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.Configuracion.route)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, null)
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = GrayAlt
                        )

                        // Cerrar sesión
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                showMenu = false
                                onLogoutClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    null,
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}