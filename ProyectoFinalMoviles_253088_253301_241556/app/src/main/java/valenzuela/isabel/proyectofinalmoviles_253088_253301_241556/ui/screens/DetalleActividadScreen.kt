package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R

@Composable
fun DetalleActividadScreen() {

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_detalleactividad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }

}

@Preview(showBackground = true)
@Composable
fun DetalleActividadPreview() {
    DetalleActividadScreen()
}