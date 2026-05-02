package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PinkSecondary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PurpleAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.HomeViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val filtros by viewModel.filtros.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_listaactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        HeaderSection(
            textoBusqueda = filtros.textoBusqueda,
            onBusquedaChange = { viewModel.setBusqueda(it) }
        )

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
                ActividadesSection(viewModel = viewModel)

            }
        }
    }
}

// Sección para el header inicial
@Composable
fun HeaderSection(
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
                text = "¡Hola, JuanPe_22!",
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
                    color = GrayAlt
                )
            }

            items(intereses) { interes ->
                Chip(
                    text = interes.label,
                    selected = seleccionado == interes.ordinal,
                    onClick = { viewModel.setInteres(interes.ordinal) },
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
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDatePicker = true },
                    value = filtros.fecha?.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("dd/mm/aaaa") },
                    trailingIcon = {
                        IconButton(
                            onClick = { mostrarDatePicker = true }
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    }
                )

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

@Composable
fun ActividadesSection(
    viewModel: HomeViewModel
) {
    val actividades by viewModel.actividades.collectAsState()

    LazyColumn {
        items(actividades) { item ->

            Column(modifier = Modifier
                .padding(8.dp)
            ) {
                Text(item.actividad.nombre, fontWeight = FontWeight.Bold)
                Text(item.interes.nombre.label)
                Text("Por: ${item.creador.nombre}")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}