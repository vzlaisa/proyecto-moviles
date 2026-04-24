package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme

@Composable
fun MainScreen(
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val modifierBoton = Modifier.width(200.dp)

    Box(modifier = Modifier.fillMaxSize()) {

        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo_mainscreen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo de Joinly",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(290.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Hagamos algo juntos",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Thin)
            )

            Spacer(Modifier.height(50.dp))

            // Botón login
            BotonPrincipal(
                text = "Iniciar sesión",
                onClick = { onLoginClick() },
                modifier = modifierBoton
            )

            Spacer(Modifier.height(20.dp))

            // Botón registrarse
            BotonPrincipal(
                text = "Registrarse",
                onClick = { onSignUpClick() },
                modifierBoton
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        MainScreen({}, {})
    }
}