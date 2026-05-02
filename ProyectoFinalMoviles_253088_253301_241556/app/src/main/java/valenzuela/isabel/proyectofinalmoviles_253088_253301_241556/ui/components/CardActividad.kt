package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White

@Composable
fun CardActividad(
    content: @Composable () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            content()
        }
    }

}