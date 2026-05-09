package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Black
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetHuella(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() } // La rayita de arriba para cerrar
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp), // Espacio extra abajo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seguridad biométrica",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "¿Deseas habilitar el inicio de sesión con tu huella digital? Puedes modificarlo después en Configuración",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Botón de confirmar
            BotonPrincipal(
                text = "Sí, habilitar huella",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            )

            // Botón de cancelar
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Ahora no", color = Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SheetHuellaPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        SheetHuella({}, {})
    }
}