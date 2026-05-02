package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardActividad

@Composable
fun DetalleActividadScreen() {

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_detalleactividad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        CardActividad {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
            ) {

            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DetalleActividadPreview() {
    DetalleActividadScreen()
}