package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PinkSecondary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PurpleAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.HomeViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val filtros by viewModel.filtros.collectAsState()

    val nickname by viewModel.nickname.collectAsState()

    var actividadSeleccionada by remember {
        mutableStateOf<ActividadConDetalle?>(null)
    }

    val context = LocalContext.current

    // Lanzador de permisos
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) viewModel.cargarUbicacion(context)
    }

    // Pedir permiso al entrar a la pantalla
    LaunchedEffect(Unit) {
        val permiso = android.Manifest.permission.ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.cargarUbicacion(context)
            }
            else -> locationPermissionLauncher.launch(permiso)
        }
    }

    // Interceptar botón físico de atrás cuando hay detalle abierto
    BackHandler(enabled = actividadSeleccionada != null) {
        actividadSeleccionada = null
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_listaactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        HeaderSection(
            nickname = nickname,
            textoBusqueda = filtros.textoBusqueda,
            onBusquedaChange = { viewModel.setBusqueda(it) }
        )

        if (actividadSeleccionada == null) {
            CardFondo {
                Column(modifier = Modifier
                    .fillMaxSize()
                    // .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    CategoriasSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(18.dp))
                    FiltrosSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(16.dp))
                    ActividadesSection(
                        viewModel = viewModel,
                        onClickActividad = { actividadSeleccionada = it }
                    )
                }
            }
        } else {
            DetalleActividadScreen(
                actividad = actividadSeleccionada!!,
                usuarioActualId = viewModel.usuarioActualId,
                onRegresar = { actividadSeleccionada = null },
                onEditar = {},
                onEliminar = { actividadSeleccionada = null }
            )
        }

    }
}

// Sección para el header inicial
@Composable
fun HeaderSection(
    nickname: String,
    textoBusqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Spacer(modifier = Modifier.height(40.dp))
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¡Hola, ${nickname}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Encuentra actividades cerca de ti...",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Buscador de actividades
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth(),
            value = textoBusqueda,
            onValueChange = onBusquedaChange,
            placeholder = { Text(text = "Buscar actividades") },
            shape = RoundedCornerShape(50),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GrayAlt,
                unfocusedBorderColor = GrayAlt,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

// Sección para las categorías
@Composable
fun CategoriasSection(
    viewModel: HomeViewModel
) {
    val filtros by viewModel.filtros.collectAsState()
    val seleccionado = filtros.idInteres
    val intereses = Interes.values().toList()

    Column {
        Text(
            text = "Categoría",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Chip(
                    text = "Todos",
                    selected = seleccionado == null,
                    onClick = { viewModel.setInteres(null) },
                    color = GrayEnabled
                )
            }

            items(intereses) { interes ->
                Chip(
                    text = interes.label,
                    selected = seleccionado == interes.ordinal,
                    onClick = { viewModel.setInteres(interes.ordinal + 1) },
                    color = getColorByInteres(interes)
                )
            }
        }
    }

}

// Elemento para los intereses
@Composable
fun Chip(
    text: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .background(
            if (selected) color else color.copy(alpha = 0.4f),
            shape = RoundedCornerShape(50)
        )
        .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Color.Black
        )
    }
}

// Función para asignar colores a los intereses
private fun getColorByInteres(interes: Interes): Color {
    return when (interes) {
        Interes.DEPORTE, Interes.JUEGOS, Interes.ESTUDIO, Interes.AIRE_LIBRE -> BlueAlt
        Interes.LITERATURA, Interes.ARTE, Interes.CINE -> PinkSecondary
        Interes.MUSICA, Interes.VIDEOJUEGOS, Interes.SOCIAL -> PurpleAlt
    }
}

// Sección para filtros
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosSection(
    viewModel: HomeViewModel
) {

    val filtros by viewModel.filtros.collectAsState()
    var mostrarDatePicker by remember { mutableStateOf(false) }

    Column {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Filtros adicionales",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EFE6))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Título para filtro de distancia
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Distancia: ${(filtros.distanciaMax ?: 50f).toInt()} km",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slider para definir la distancia
                Slider(
                    value = filtros.distanciaMax ?: 50f,
                    onValueChange = { viewModel.setDistancia(it) },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = OrangePrimary,
                        activeTrackColor = Color(0xFFD6C49A),
                        inactiveTrackColor = Color(0xFFEADFC8)
                    )
                )

                // Indicadores de distancia
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1 km", style = MaterialTheme.typography.bodySmall)
                    Text(text = "100 km", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Titulo para filtro de fecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Filtro para fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { mostrarDatePicker = true },
                        value = filtros.fecha?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("dd/mm/aaaa") },
                        trailingIcon = {
                            IconButton(onClick = { mostrarDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        }
                    )

                    // Botón para limpiar la fecha
                    if (filtros.fecha != null) {
                        IconButton(onClick = { viewModel.setFecha(null) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar fecha",
                                tint = GrayEnabled
                            )
                        }
                    }
                }

                // Mostrar DatePicker
                if (mostrarDatePicker) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = { mostrarDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val fecha = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    viewModel.setFecha(fecha)
                                }
                                mostrarDatePicker = false
                            }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                mostrarDatePicker = false
                            }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

            }
        }

    }
}

// Sección de actividades
@Composable
fun ActividadesSection(
    viewModel: HomeViewModel,
    onClickActividad: (ActividadConDetalle) -> Unit
) {
    val actividades by viewModel.actividades.collectAsState()

    Column {
        Text(modifier = Modifier
            .padding(bottom = 12.dp),
            text = "Actividades",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        if (actividades.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.figura_cambiarcontra),
                    contentDescription = null,
                    modifier = Modifier
                        .width(250.dp)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "No se encontraron actividades",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(actividades) { item ->
                    ActividadCard(
                        actividad = item,
                        onClick = { onClickActividad(item) }
                    )
                }
            }
        }
    }
}

// Card para mostrar actividad
@Composable
fun ActividadCard(
    actividad: ActividadConDetalle,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM · HH:mm", Locale("es"))
    val colorInteres = getColorByInteres(actividad.interes.nombre)

    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagen de la actividad
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
            ) {
                // Cambiar por async después por la imagen de la actividad
//                AsyncImage(
//                    model = actividad.actividad.imageUrl,
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(colorInteres.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = colorInteres
                    )
                }

                // Tags para actividad privada o recurrente
                Row(modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!actividad.actividad.publica) Tag("Privado")
                    if (actividad.actividad.recurrente) Tag("Recurrente")
                }
            }

            // Información de la actividad
            Column(modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = actividad.actividad.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Por: ${actividad.creador.nombre} ${actividad.creador.apellidoPaterno}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayEnabled
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ícono y descripción corta
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(colorInteres.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconByInteres(actividad.interes.nombre),
                                contentDescription = null,
                                tint = colorInteres,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = actividad.actividad.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fecha
                    Text(
                        text = actividad.actividad.fechaHora.format(dateFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorInteres,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Obtener icon por el interés
fun getIconByInteres(interes: Interes): ImageVector {
    return when (interes) {
        Interes.DEPORTE -> Icons.Default.SportsBasketball
        Interes.MUSICA -> Icons.Default.MusicNote
        Interes.LITERATURA -> Icons.Default.Book
        Interes.ESTUDIO -> Icons.Default.School
        Interes.VIDEOJUEGOS -> Icons.Default.SportsEsports
        Interes.ARTE -> Icons.Default.Palette
        Interes.JUEGOS -> Icons.Default.Casino
        Interes.SOCIAL -> Icons.Default.People
        Interes.CINE -> Icons.Default.Movie
        Interes.AIRE_LIBRE -> Icons.Default.Park
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}