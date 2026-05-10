package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.FondoOndulado
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredLabel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredTextField
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.StepIndicator
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Black
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PinkSecondary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.EditarActividadViewModel
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarActividadLayout(
    pasoActual: Int,
    tituloPaso: String,
    rutaImagen: Int,
    onClose: () -> Unit,
    onNext: () -> Unit,
    content: @Composable () -> Unit
) {
    val pasosColores = listOf(PinkSecondary, BlueAlt, OrangePrimary)

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Black)
            }
            Text(
                text = "Editar actividad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Black
            )

            if (pasoActual < 3) {
                IconButton(onClick = onNext) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Black)
                }
            } else {
                Button(
                    onClick = {
                        onNext()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Guardar", color = White, fontWeight = FontWeight.Bold)
                }
            }
        }

        FondoOndulado(rutaImagen = rutaImagen) {
            CardFondo {
                Column(modifier = Modifier.padding(30.dp)) {
                    Text(
                        text = "Edita tu actividad",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    StepIndicator(
                        pasoActual = pasoActual,
                        colores = pasosColores
                    )

                    Spacer(Modifier.height(30.dp))

                    Text(
                        text = "Paso $pasoActual",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrayEnabled
                    )
                    Text(
                        text = tituloPaso,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarActividadPaso1(
    viewModel: EditarActividadViewModel,
    onClose: () -> Unit,
    onNext: () -> Unit
) {
    // Estado para controlar la visibilidad de las sugerencias
    var showSuggestions by remember { mutableStateOf(false) }

    EditarActividadLayout(
        pasoActual = 1,
        tituloPaso = "Descripción de actividad",
        rutaImagen = R.drawable.figura_ondas_rosa,
        onClose = onClose,
        onNext = onNext
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            RequiredLabel("Nombre de la actividad")
            RequiredTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                placeholder = "Ej. Taller de acuarela"
            )

            Spacer(Modifier.height(20.dp))

            RequiredLabel("Categoría")
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = viewModel.categoria?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = GrayAlt.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Interes.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.label) },
                            onClick = {
                                viewModel.onCategoriaChange(opcion)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            RequiredLabel("Descripción")
            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                placeholder = { Text("Comparte una breve descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = White,
                    unfocusedBorderColor = GrayAlt
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))

            RequiredLabel("Ubicación")
            Box {
                OutlinedTextField(
                    value = if (showSuggestions) viewModel.queryBusqueda else viewModel.ubicacion,
                    onValueChange = {
                        showSuggestions = true
                        viewModel.onQueryBusquedaChange(it)
                    },
                    placeholder = { Text("Ej. Parque Central o Calle 123") },
                    trailingIcon = {
                        if (viewModel.buscandoUbicacion) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (showSuggestions && viewModel.resultadosBusqueda.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 65.dp)
                            .zIndex(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            viewModel.resultadosBusqueda.forEach { resultado ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = resultado.nombreFormateado,
                                            maxLines = 2,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    onClick = {
                                        viewModel.onUbicacionSeleccionada(
                                            nombre = resultado.nombreFormateado,
                                            lat = resultado.latitud.toDouble(),
                                            lon = resultado.longitud.toDouble()
                                        )
                                        showSuggestions = false
                                    }
                                )
                                HorizontalDivider(color = GrayAlt.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarActividadPaso2(
    viewModel: EditarActividadViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    EditarActividadLayout(
        pasoActual = 2,
        tituloPaso = "Tiempos",
        rutaImagen = R.drawable.figura_ondas_azul,
        onClose = onClose,
        onNext = onNext
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            RequiredLabel("Fecha")
            var mostrarDatePickerFecha by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.fecha?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona una fecha") },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePickerFecha = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (mostrarDatePickerFecha) {
                val datePickerState = rememberDatePickerState(
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= System.currentTimeMillis() - 86400000
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePickerFecha = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val fechaSel = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                viewModel.onFechaChange(fechaSel)
                            }
                            mostrarDatePickerFecha = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePickerFecha = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(Modifier.height(20.dp))

            RequiredLabel("Hora")
            var mostrarTimePicker by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.hora?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona una hora") },
                trailingIcon = {
                    IconButton(onClick = { mostrarTimePicker = true }) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (mostrarTimePicker) {
                val timePickerState = rememberTimePickerState()
                Dialog(
                    onDismissRequest = { mostrarTimePicker = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    CardFondo {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TimePicker(state = timePickerState)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { mostrarTimePicker = false }) {
                                    Text("Cancelar")
                                }
                                TextButton(onClick = {
                                    viewModel.onHoraChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                                    mostrarTimePicker = false
                                }) {
                                    Text("Aceptar")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            RequiredLabel("Fecha límite para confirmar")
            var mostrarDatePickerLimite by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.fechaLimite?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Selecciona una fecha") },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePickerLimite = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (mostrarDatePickerLimite) {
                val datePickerStateLimite = rememberDatePickerState(
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            val fechaActividad = viewModel.fecha
                            return if (fechaActividad != null) {
                                val limiteMillis = fechaActividad.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                utcTimeMillis <= limiteMillis
                            } else {
                                true
                            }
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePickerLimite = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerStateLimite.selectedDateMillis?.let { millis ->
                                val fechaSel = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                viewModel.onFechaLimiteChange(fechaSel)
                            }
                            mostrarDatePickerLimite = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePickerLimite = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerStateLimite)
                }
            }

            Spacer(Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { onBack() }
                ) {
                    Text("Regresar")
                }
            }
        }
    }
}

@Composable
fun EditarActividadPaso3(
    viewModel: EditarActividadViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onGuardar: () -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onFotoChange(uri)
        }
    )

    LaunchedEffect(viewModel.actualizacionExitosa) {
        if (viewModel.actualizacionExitosa) {
            Toast.makeText(context, "¡Actividad actualizada con éxito!", Toast.LENGTH_SHORT).show()
            viewModel.limpiarDatos()
            onGuardar()
        }
    }

    LaunchedEffect(viewModel.actualizacionError) {
        viewModel.actualizacionError?.let { mensajeError ->
            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
        }
    }

    EditarActividadLayout(
        pasoActual = 3,
        tituloPaso = "Configuración",
        rutaImagen = R.drawable.figura_ondas_naranja,
        onClose = onClose,
        onNext = {
            viewModel.guardarCambios(context)
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            RequiredLabel("Número máximo de participantes")
            OutlinedTextField(
                value = viewModel.maxParticipantes,
                onValueChange = { viewModel.onMaxParticipantesChange(it) },
                placeholder = { Text("Ej. 10") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = GrayAlt.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Actividad privada", fontWeight = FontWeight.Medium)
                    Text(
                        text = "Las actividades privadas requieren aceptar solicitudes. Por defecto, las actividades son públicas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayEnabled,
                        lineHeight = 14.sp
                    )
                }
                Switch(
                    checked = viewModel.isPrivada,
                    onCheckedChange = { viewModel.onPrivadaChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = Black,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = GrayEnabled
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Actividad recurrente", fontWeight = FontWeight.Medium)
                    Text(
                        text = "Por defecto, las actividades no son recurrentes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrayEnabled
                    )
                }
                Switch(
                    checked = viewModel.isRecurrente,
                    onCheckedChange = { viewModel.onRecurrenteChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = Black,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = GrayEnabled
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Foto de portada", fontWeight = FontWeight.Medium)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrayAlt)
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                // Aquí aplicamos la lógica de mostrar la foto nueva o la que ya tenía
                if (viewModel.fotoUri != null) {
                    AsyncImage(
                        model = viewModel.fotoUri,
                        contentDescription = "Preview de la foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (viewModel.urlImagenActual != null) {
                    AsyncImage(
                        model = viewModel.urlImagenActual,
                        contentDescription = "Preview de la foto actual",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = GrayEnabled,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tip: \"Sube una foto que inspire\".",
                style = MaterialTheme.typography.labelSmall,
                color = GrayEnabled
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrayAlt, contentColor = Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cambiar imagen")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { viewModel.onFotoChange(null) }) {
                    Text("Dejar la original", color = Black)
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { onBack() }) {
                    Text("Regresar")
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}