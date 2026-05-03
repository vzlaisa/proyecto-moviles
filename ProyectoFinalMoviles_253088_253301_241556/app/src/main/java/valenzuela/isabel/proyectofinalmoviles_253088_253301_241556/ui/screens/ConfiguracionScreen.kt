package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.SmallTopAppBar
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.LightBlue
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.ConfigViewModel

@Composable
fun ConfiguracionScreen(
    onBack: () -> Unit,
    viewModel: ConfigViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_listaactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        SmallTopAppBar(
            title = "Configuración",
            onBack = { onBack() },
        )

        CardFondo {
            val checked by viewModel.biometricoEnabled.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            ) {
                Card(
                ) {
                    Row(
                        modifier = Modifier
                            .background(LightBlue)
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Inicio de sesión biométrico")

                        Spacer(Modifier.weight(1f))

                        Switch(
                            checked = checked,
                            onCheckedChange = { viewModel.onBiometricoChanged(it) }
                        )
                    }

                }
            }
        }
    }
}