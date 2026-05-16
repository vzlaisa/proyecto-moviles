package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardNotificacion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.SmallTopAppBar
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.DateUtils.toNotificationString
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.NotificacionViewModel

@Composable
fun NotificacionesScreen(
    onBack: () -> Unit,
    onNotificacionClick: (Int) -> Unit,
    viewModel: NotificacionViewModel
) {
    val notificaciones by viewModel.notificaciones.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_listaactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        SmallTopAppBar(
            title = "Notificaciones",
            onBack = { onBack() },
        )

        CardFondo {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (notificaciones.isEmpty()) {
                    Text(
                        text = "No hay notificaciones para mostrar",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notificaciones) { notificacion ->
                            CardNotificacion(
                                titulo = notificacion.titulo,
                                mensaje = notificacion.mensaje,
                                fecha = notificacion.fecha.toNotificationString(),
                                leida = notificacion.leida,
                                onClick = {
                                    notificacion.idActividad?.let {
                                        onNotificacionClick(it)
                                        viewModel.marcarComoLeida(notificacion.firestoreId)
                                    }
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificacionesPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {

    }
}