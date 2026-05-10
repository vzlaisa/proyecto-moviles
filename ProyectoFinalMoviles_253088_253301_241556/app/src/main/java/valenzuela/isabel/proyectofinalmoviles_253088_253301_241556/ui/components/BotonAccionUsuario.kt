package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import java.time.LocalDateTime

@Composable
fun BotonAccionUsuario(
    actividad: ActividadEntity,
    estado: EstadoInscripcion?,
    cargando: Boolean,
    onUnirse: () -> Unit,
    onAbandonar: () -> Unit,
    onConfirmarAsistencia: () -> Unit
) {
    val ahora = LocalDateTime.now()
    val antesLimite = actividad.fechaLimite?.isAfter(ahora) ?: false

    if (cargando) {
        Box(modifier = Modifier
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = OrangePrimary)
        }
        return
    }

    when (estado) {
        null -> {
            BotonPrincipal(
                modifier = Modifier.fillMaxWidth(),
                text = if (actividad.publica) "Unirse" else "Solicitar unirse",
                onClick = onUnirse
            )
        }
        EstadoInscripcion.PENDIENTE -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedButton(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                    onClick = onAbandonar,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text(
                        "Cancelar solicitud",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Solicitud pendiente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayEnabled
                )
            }
        }
        EstadoInscripcion.CONFIRMADO -> {
            Column {
                if (antesLimite) {
                    BotonPrincipal(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Confirmar asistencia",
                        onClick = onConfirmarAsistencia
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                OutlinedButton(
                    onClick = onAbandonar,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text(
                        "Abandonar actividad",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        EstadoInscripcion.ASISTENCIA_CONFIRMADA -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(24.dp)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Asistencia confirmada", color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium)
            }
        }
        EstadoInscripcion.CANCELADO -> {
            BotonPrincipal(
                modifier = Modifier.fillMaxWidth(),
                text = if (actividad.publica) "Unirse de nuevo" else "Solicitar unirse",
                onClick = onUnirse
            )
        }

    }
}