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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import java.time.Instant

@Composable
fun HomeScreen() {

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.fondo_listaactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        HeaderSection()

        CardFondo {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                CategoriasSection()

                Spacer(modifier = Modifier.height(18.dp))
                FiltrosSection()

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}

// Sección para el header inicial
@Composable
fun HeaderSection() {
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
            value = "",
            onValueChange = {},
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
fun CategoriasSection() {
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
            items(intereses) { interes ->
                Chip(
                    text = interes.label,
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
    color: Color
) {
    Box(modifier = Modifier
        .background(color, shape = RoundedCornerShape(50))
        .clickable {}
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text)
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
@Composable
fun FiltrosSection() {

    var distancia by remember { mutableStateOf(50f) }
    var fecha by remember { mutableStateOf("") }

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
                        text = "Distancia: ${distancia.toInt()} km",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slider para definir la distancia
                Slider(
                    value = distancia,
                    onValueChange = { distancia = it },
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
                FiltroFecha()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun FiltroFecha() {
    var fecha by remember { mutableStateOf<LocalDate?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = OrangePrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarDatePicker = true },
                value = fecha?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("dd/mm/aaaa") },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFF7F7F7),
                    unfocusedContainerColor = Color(0xFFF7F7F7)
                )
            )
        }
        // Mostrar Date Picker
        if (mostrarDatePicker) {
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= System.currentTimeMillis()
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = { mostrarDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fecha = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        mostrarDatePicker = false
                    }) {
                        Text(text = "Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDatePicker = false }) {
                        Text(text = "Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}