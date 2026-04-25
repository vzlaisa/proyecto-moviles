package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Black
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt

@Composable
fun StepIndicator(
    pasoActual: Int,
    colores: List<Color>,  // un color por paso
    colorInactivo: Color = GrayAlt,
    modifier: Modifier = Modifier
) {
    val totalPasos = colores.size

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        colores.forEachIndexed { index, color ->
            val paso = index + 1
            val colorCirculo = if (paso <= pasoActual) color else colorInactivo

            // Círculo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorCirculo)
            ) {
                Text(
                    text = paso.toString(),
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Línea entre pasos
            if (paso < totalPasos) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Black)
                )
            }
        }
    }
}