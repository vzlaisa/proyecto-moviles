package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.SmallTopAppBar
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionConUsuario
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonAccionUsuario
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.state.UiEstado
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.DetalleActividadViewModel

@Composable
fun DetalleActividadScreen(
    actividad: ActividadConDetalle,
    viewModel: DetalleActividadViewModel,
    onRegresar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val usuarioActualId by viewModel.usuarioActualId.collectAsState()
    val estadoInscripcion by viewModel.estadoInscripcion.collectAsState()
    val participantes by viewModel.participantes.collectAsState()
    val uiEstado by viewModel.uiEstado.collectAsState()

    val participantesConfirmados by viewModel.participantesConfirmados.collectAsState()
    val solicitudesPendientes by viewModel.solicitudesPendientes.collectAsState()

    val colorInteres = getColorByInteres(actividad.interes.nombre)

    val esCreador = actividad.actividad.idCreador == usuarioActualId
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // Cargar datos al entrar
    LaunchedEffect(actividad.actividad.id) {
        viewModel.cargar(actividad)
    }

    // Limpiar datos al salir
    DisposableEffect(Unit) {
        onDispose { viewModel.limpiar() }
    }

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
                    HeaderImagen(actividad.actividad, colorInteres = colorInteres)
                    Spacer(modifier = Modifier.height(16.dp))

                    TituloActividad(actividad, colorInteres)
                    Spacer(modifier = Modifier.height(20.dp))

                    InfoActividad(actividad = actividad.actividad, totalParticipantes = participantes.size, colorInteres = colorInteres)

                    HorizontalDivider(color = GrayAlt, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    DescripcionActividad(actividad.actividad, colorInteres = colorInteres)

                    HorizontalDivider(color = GrayAlt, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                    SeccionParticipantesConfirmados(
                        idCreador = actividad.actividad.idCreador,
                        inscripciones = participantesConfirmados,
                        esCreador = esCreador,
                        colorInteres = colorInteres,
                        onExpulsar = { idUsuario -> viewModel.expulsarParticipante(idUsuario) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mostrar botón para unirse si no es creador
                    if (!esCreador) {
                        BotonAccionUsuario(
                            actividad = actividad.actividad,
                            estado = estadoInscripcion,
                            cargando = uiEstado is UiEstado.Cargando,
                            onUnirse = { viewModel.unirse() },
                            onAbandonar = { viewModel.abandonar() },
                            onConfirmarAsistencia = { viewModel.confirmarAsistencia() }
                        )
                    }

                    // Mostrar solicitudes pendientes solo al creador y si la actividad es privada
                    if (esCreador && !actividad.actividad.publica && solicitudesPendientes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SeccionSolicitudesPendientes(
                            solicitudes = solicitudesPendientes,
                            colorInteres = colorInteres,
                            onAceptar = { idUsuario -> viewModel.aceptarSolicitud(idUsuario) },
                            onRechazar = { idUsuario -> viewModel.rechazarSolicitud(idUsuario) }
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
fun HeaderImagen(
    actividad: ActividadEntity,
    colorInteres: Color
) {
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
                Tag(texto = "Privado", icono = Icons.Outlined.Lock, colorInteres = colorInteres)
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (actividad.recurrente) {
                Tag(texto = "Recurrente", icono = Icons.Outlined.Repeat, colorInteres = colorInteres)
            }
        }
    }
}

@Composable
fun Tag(
    texto: String,
    icono: ImageVector? = null,
    colorInteres: Color
) {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.9f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(imageVector = icono, contentDescription = null, tint = colorInteres, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(texto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colorInteres)
    }
}

@Composable
fun TituloActividad(
    actividad: ActividadConDetalle,
    colorInteres: Color
) {
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
                    .background(colorInteres.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(actividad.interes.nombre.label, fontSize = 12.sp, color = colorInteres, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Organizado por: ${actividad.creador.nombreCompleto}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )
            if (!actividad.actividad.publica) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier
                        .background(colorInteres.copy(alpha = 0.15f),RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = colorInteres,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Requiere aprobación",
                        color = colorInteres,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InfoActividad(
    actividad: ActividadEntity,
    totalParticipantes: Int,
    colorInteres: Color
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1.2f)) {
            InfoItem(Icons.Default.DateRange, "Fecha y hora", "${actividad.fechaHora.format(dateFormatter)}\n${actividad.fechaHora.format(timeFormatter)} hrs", colorInteres = colorInteres)
            Spacer(modifier = Modifier.height(16.dp))
            InfoItem(Icons.Default.LocationOn, "Ubicación", actividad.ubicacion, colorInteres = colorInteres)
            Spacer(modifier = Modifier.height(16.dp))
            InfoItem(Icons.Default.Group, "Participantes",
                "$totalParticipantes/${actividad.maxParticipantes}", colorInteres = colorInteres)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(0.8f)) {
            if (actividad.recurrente) {
                InfoItem(Icons.Outlined.Repeat, "Actividad recurrente", "", colorInteres)
                Spacer(modifier = Modifier.height(16.dp))
            }
            actividad.fechaLimite?.let {
                InfoItem(Icons.Default.EventAvailable, "Confirmar antes de", it.format(dateFormatter), colorInteres)
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    titulo: String,
    valor: String,
    colorInteres: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = colorInteres, modifier = Modifier.size(24.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            if (titulo.isNotBlank()) {
                Text(titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Black)
            }
            Text(valor, style = MaterialTheme.typography.bodyMedium, color = Black)
        }
    }
}

@Composable
fun DescripcionActividad(
    actividad: ActividadEntity,
    colorInteres: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = colorInteres, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Descripción", fontWeight = FontWeight.Bold, color = Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(actividad.descripcion, style = MaterialTheme.typography.bodyMedium, color = Black)
    }
}


@Composable
fun SeccionParticipantesConfirmados(
    idCreador: Int,
    inscripciones: List<InscripcionConUsuario>,
    esCreador: Boolean,
    colorInteres: Color,
    onExpulsar: (Int) -> Unit
) {
    Column {
        Text("Participantes Confirmados:", fontWeight = FontWeight.Bold, color = Black)
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(180.dp)
                .background(colorInteres.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (inscripciones.isNotEmpty()) {
                LazyColumn {
                    items(inscripciones) { item ->
                        Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Black
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.usuario.nombreCompleto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Black
                                )
                                if (item.inscripcion.idUsuario == idCreador) {
                                    Text(
                                        "(Organizador)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OrangePrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (esCreador && item.inscripcion.idUsuario != idCreador) {
                                IconButton(
                                    onClick = { onExpulsar(item.inscripcion.idUsuario) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Expulsar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Aún no hay participantes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun SeccionSolicitudesPendientes(
    solicitudes: List<InscripcionConUsuario>,
    colorInteres: Color,
    onAceptar: (Int) -> Unit,
    onRechazar: (Int) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = colorInteres,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Solicitudes Pendientes",
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.weight(1f)
            )
            // Contador
            Box(modifier = Modifier
                .background(colorInteres, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${solicitudes.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth()
            .heightIn(min = 60.dp, max = 220.dp)
            .background(colorInteres.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(12.dp)
        ) {
            LazyColumn {
                items(solicitudes) { item ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.usuario.nombreCompleto,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Black
                            )
                            Text(
                                text = item.inscripcion.fechaInscripcion
                                    .format(DateTimeFormatter.ofPattern("d 'de' MMMM, HH:mm", Locale("es"))),
                                style = MaterialTheme.typography.bodySmall,
                                color = GrayEnabled
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Botón para aceptar
                            Box(modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .clickable { onAceptar(item.inscripcion.idUsuario) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Aceptar",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Botón para rechazar
                            Box(modifier = Modifier
                                .size(36.dp)
                                .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .clickable { onRechazar(item.inscripcion.idUsuario) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Rechazar",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}