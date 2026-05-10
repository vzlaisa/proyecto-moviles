package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.SmallTopAppBar
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun DetalleActividadScreen(
    actividad: ActividadConDetalle,
    usuarioActualId: Int,
    onRegresar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val esCreador = actividad.actividad.idCreador == usuarioActualId
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_detalleactividad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SmallTopAppBar(
                title = "Detalle de actividad",
                onBack = onRegresar,
                actionContent = {
                    if (esCreador) {
                        Row {
                            IconButton(onClick = onEditar) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = BlueLink
                                )
                            }
                            IconButton(onClick = { mostrarDialogoEliminar = true }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            )

            CardFondo {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    HeaderImagen(actividad.actividad)
                    Spacer(modifier = Modifier.height(16.dp))

                    TituloActividad(actividad)
                    Spacer(modifier = Modifier.height(20.dp))

                    InfoActividad(actividad.actividad)

                    HorizontalDivider(color = GrayAlt, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    DescripcionActividad(actividad.actividad)

                    HorizontalDivider(color = GrayAlt, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    HorizontalDivider(color = GrayAlt, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    SeccionParticipantesConfirmados()

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!esCreador) {
                        BotonPrincipal(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (actividad.actividad.publica) "Unirse" else "Solicitar unirse",
                            onClick = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (mostrarDialogoEliminar) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoEliminar = false },
                    title = {
                        Text(text = "Eliminar actividad")
                    },
                    text = {
                        Text(text = "¿Estás seguro de que deseas eliminar esta actividad? Esta acción no se puede deshacer.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mostrarDialogoEliminar = false
                                onEliminar()
                            }
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { mostrarDialogoEliminar = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderImagen(actividad: ActividadEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        AsyncImage(
            model = actividad.imageUrl ?: R.drawable.default_activity_cover,
            contentDescription = "Portada de actividad",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(modifier = Modifier.padding(12.dp)) {
            if (!actividad.publica) {
                Tag(texto = "Privado", icono = Icons.Outlined.Lock)
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (actividad.recurrente) {
                Tag(texto = "Recurrente", icono = Icons.Outlined.Repeat)
            }
        }
    }
}

@Composable
fun Tag(texto: String, icono: ImageVector? = null) {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.9f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(imageVector = icono, contentDescription = null, tint = PurpleAlt, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(texto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PurpleAlt)
    }
}

@Composable
fun TituloActividad(actividad: ActividadConDetalle) {
    Column {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = actividad.actividad.nombre,
                style = MaterialTheme.typography.headlineSmall,
                color = Black,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(PurpleAlt.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(actividad.interes.nombre.label, fontSize = 12.sp, color = PurpleAlt, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Organizado por: ${actividad.creador.nombreCompleto}",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayEnabled
            )
            if (!actividad.actividad.publica) {
                Spacer(modifier = Modifier.weight(1f))
                Tag(texto = "Requiere aprobación")
            }
        }
    }
}

@Composable
fun InfoActividad(actividad: ActividadEntity) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1.2f)) {
            InfoItem(Icons.Default.DateRange, "Fecha y hora", "${actividad.fechaHora.format(dateFormatter)}\n${actividad.fechaHora.format(timeFormatter)} hrs")
            Spacer(modifier = Modifier.height(16.dp))
            InfoItem(Icons.Default.LocationOn, "Ubicación", actividad.ubicacion)
            Spacer(modifier = Modifier.height(16.dp))
            InfoItem(Icons.Default.Group, "Participantes", "0/${actividad.maxParticipantes}")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(0.8f)) {
            if (actividad.recurrente) {
                InfoItem(Icons.Outlined.Repeat, "", "Cada primer viernes del mes")
                Spacer(modifier = Modifier.height(16.dp))
            }
            actividad.fechaLimite?.let {
                InfoItem(Icons.Default.EventAvailable, "Confirmar antes de", it.format(dateFormatter))
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, titulo: String, valor: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = PurpleAlt, modifier = Modifier.size(24.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            if (titulo.isNotBlank()) {
                Text(titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Black)
            }
            Text(valor, style = MaterialTheme.typography.bodyMedium, color = GrayEnabled)
        }
    }
}

@Composable
fun DescripcionActividad(actividad: ActividadEntity) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PurpleAlt, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Descripción", fontWeight = FontWeight.Bold, color = Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(actividad.descripcion, style = MaterialTheme.typography.bodyMedium, color = GrayEnabled)
    }
}


@Composable
fun SeccionParticipantesConfirmados(participantes: List<UsuarioEntity> = emptyList()) {
    Column {
        Text("Participantes Confirmados:", fontWeight = FontWeight.Bold, color = Black)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(180.dp)
                .background(Color(0xFFF8F8F8), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (participantes.isEmpty()) {
                Text("Aún no hay participantes", style = MaterialTheme.typography.bodyMedium, color = GrayEnabled, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(participantes) { usuario ->
                        Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp), tint = GrayEnabled)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${usuario.nombre} ${usuario.apellidoPaterno}", style = MaterialTheme.typography.bodyMedium, color = Black)
                        }
                    }
                }
            }
        }
    }
}