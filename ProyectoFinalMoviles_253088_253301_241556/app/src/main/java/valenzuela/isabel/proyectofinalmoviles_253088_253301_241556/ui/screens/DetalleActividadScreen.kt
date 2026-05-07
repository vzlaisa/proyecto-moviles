package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardActividad
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PurpleAlt
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetalleActividadScreen(
    actividad: ActividadConDetalle,
    usuarioActualId: Int,
    onRegresar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {

    val esCreador = actividad.actividad.idCreador == usuarioActualId

    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }

            Text(modifier = Modifier.weight(1f),
                text = "Detalle de la Actividad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Configuraciones para creador de actividad
            if (esCreador) {
                Row {
                    // Ícono para editar
                    IconButton(onClick = onEditar) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.Cyan
                        )
                    }
                    // Ícono para eliminar
                    IconButton(onClick = onEliminar) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_detalleactividad),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        CardActividad {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
            ) {
                HeaderImagen(actividad.actividad)
                Spacer(modifier = Modifier.height(12.dp))
                TituloActividad(actividad)
                Spacer(modifier = Modifier.height(12.dp))
                InfoActividad(actividad.actividad)

                Divider(color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                DescripcionActividad(actividad.actividad)

                Divider(color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SeccionRequisitos()

                Divider(color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SeccionParticipantesConfirmados()
                Spacer(modifier = Modifier.height(20.dp))

                BotonPrincipal(modifier = Modifier
                    .fillMaxWidth(),
                    text = if (actividad.actividad.publica) "Unirse" else "Solicitar Unirse",
                    onClick = {}
                )
            }
        }

    }
}

// Imagen de la actividad
@Composable
fun HeaderImagen(actividad: ActividadEntity) {
    Box {
        Image(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp)),
            painter = painterResource(R.drawable.figura_cambiarcontra), // cambiar por imagen de actividad
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Row(modifier = Modifier.padding(8.dp)) {
            if (!actividad.publica) {
                Tag("Privado")
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (actividad.recurrente) {
                Tag("Recurrente")
            }
        }
    }
}

// Tags dentro de la imagen
@Composable
fun Tag(text: String) {
    Box(modifier = Modifier
        .background(Color.White, RoundedCornerShape(50))
        .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

// Titulo de la actividad
@Composable
fun TituloActividad(
    actividad: ActividadConDetalle
) {
    Column {
        Text(
            text = actividad.actividad.nombre,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Organizado por: ${actividad.creador.nombre}")
            Spacer(modifier = Modifier.weight(1f))
            ChipMini(actividad.interes.nombre.label)
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

// Enseñar el interes al que pertenece la actividad
@Composable
fun ChipMini(text: String) {
    Box(modifier = Modifier
        .background(Color.White, RoundedCornerShape(50))
        .padding(horizontal = 10.dp, vertical = 4.dp)

    ) {
        Text(text, fontSize = 12.sp)
    }
}

// Información de la actividad
@Composable
fun InfoActividad(
    actividad: ActividadEntity
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column {
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                InfoItem(
                    icon = Icons.Default.DateRange,
                    titulo = "Fecha y hora",
                    valor = actividad.fechaHora.format(dateFormatter)
                )

                InfoItem(
                    icon = Icons.Default.AccessTime,
                    titulo = "",
                    valor = "${actividad.fechaHora.format(timeFormatter)} hrs"
                )

                Spacer(modifier = Modifier.height(8.dp))
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    titulo = "Ubicación",
                    valor = actividad.ubicacion
                )

                Spacer(modifier = Modifier.height(8.dp))
                InfoItem(
                    icon = Icons.Default.Group,
                    titulo = "Participantes",
                    valor = "0/${actividad.maxParticipantes}" // conectar luego para llevar cuenta de participantes unidos
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (actividad.recurrente) {
                    InfoItem(
                        icon = Icons.Default.Repeat,
                        titulo = "",
                        valor = "Cada primer viernes del mes" // cambiar para la de cada actividad
                    )
                }

                actividad.fechaLimite?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(
                        icon = Icons.Default.EventAvailable,
                        titulo = "Confirmar antes de ",
                        valor = it.format(dateFormatter)
                    )
                }
            }
        }
    }
}

//Item para la información
@Composable
fun InfoItem(
    icon: ImageVector,
    titulo: String,
    valor: String
) {
    Row(modifier = Modifier
        .padding(bottom = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(modifier = Modifier
            .size(20.dp),
            imageVector = icon,
            contentDescription = null,
            tint = PurpleAlt // Cambiar dependiendo del interés
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            if (titulo.isNotBlank()) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                color = GrayEnabled
            )
        }
    }
}

// Descripción de la actividad
@Composable
fun DescripcionActividad(actividad: ActividadEntity) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = PurpleAlt
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Descripción", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(actividad.descripcion)
    }
}

// Sección de requisitos (harcodeado por ahora, cambiar para mostrar datos reales)
@Composable
fun SeccionRequisitos(
    requisitos: List<String> = listOf("No se necesita experiencia previa", "Edad mínima: 12+ años"),
    queLlevar: List<String> = listOf("Ropa cómoda que se pueda ensuciar", "Delantal (opcional)")
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = PurpleAlt
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Requisitos", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        requisitos.forEach {
            Text("• $it")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = PurpleAlt
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Qué llevar", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        queLlevar.forEach {
            Text("• $it")
        }
    }
}

// Seccion para participantes confirmados
@Composable
fun SeccionParticipantesConfirmados(
    participantes: List<UsuarioEntity> = emptyList()
) {
    Column {
        Text("Participantes Confirmados", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .height(140.dp)
            .background(GrayAlt, RoundedCornerShape(12.dp))
            .padding(8.dp)
        ) {
            if (participantes.isEmpty()) {
                Text(
                    text = "Aún no hay participantes",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                LazyColumn {
                    items(participantes) { usuario ->
                        Row(modifier = Modifier
                            .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleActividadPreview() {
    // DetalleActividadScreen()
}