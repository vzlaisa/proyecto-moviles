package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun RequiredLabel(text: String) {
    Text(
        buildAnnotatedString {
            append(text)

            withStyle(
                SpanStyle(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" *")
            }
        }
    )
}