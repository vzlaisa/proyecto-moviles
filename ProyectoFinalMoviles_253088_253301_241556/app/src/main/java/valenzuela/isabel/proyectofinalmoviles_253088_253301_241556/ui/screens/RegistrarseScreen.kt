package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.FondoOndulado
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredLabel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.StepIndicator
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Black
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Green
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PinkSecondary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PurpleAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.RegistroViewModel
import java.io.File
import java.time.Instant
import java.time.ZoneId
import coil.compose.AsyncImage

@Composable
private fun RegistroLayout(
    pasoActual: Int,
    tituloPaso: String,
    rutaImagen: Int,
    content: @Composable () -> Unit
) {
    val pasosColores = listOf(
        PinkSecondary,
        BlueAlt,
        OrangePrimary,
        PurpleAlt
    )

    FondoOndulado(rutaImagen = rutaImagen) {
        CardFondo {
            Column(modifier = Modifier.padding(40.dp)) {
                Text(
                    text = "Crear una cuenta",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                StepIndicator(
                    pasoActual = pasoActual,
                    colores = pasosColores
                )

                Spacer(Modifier.height(33.dp))

                Text(
                    text = "Paso $pasoActual",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = tituloPaso,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(25.dp))

                // Contenido de cada paso
                content()
            }
        }
    }
}

// Paso 1 del wizard
@Composable
fun RegistroPaso1(
    onNext: () -> Unit,
    viewModel : RegistroViewModel
) {
    RegistroLayout(1, "Información de acceso", R.drawable.figura_ondas_rosa) {

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            // Correo
            RequiredLabel("Correo electrónico")
            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Ej. correo@gmail.com") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Contraseña
            RequiredLabel("Contraseña")
            OutlinedTextField(
                value = viewModel.pass,
                onValueChange = { viewModel.onPassChange(it) },
                label = { Text("Ingresa tu contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Confirmar contraseña
            RequiredLabel("Confirmar contraseña")
            OutlinedTextField(
                value = viewModel.confirmarPass,
                onValueChange = { viewModel.onConfirmarPassChange(it) },
                label = { Text("Confirma tu contraseña") },
                isError = viewModel.mostrarErrorPass,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) viewModel.onConfirmarPassFocusLost()
                    }
            )

            Spacer(Modifier.height(10.dp))

            // Para error si las contraseñas no coinciden
            if (viewModel.mostrarErrorPass) {
                Text(
                    text = "Las contraseñas no coinciden",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(20.dp))

            // Reglas de contraseña
            viewModel.reglas.forEach { (texto, cumple) ->
                RowReglaContraseña(texto = texto, cumple = cumple)
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(Modifier.height(20.dp))

            NavegacionPasos(
                onNext = { if (viewModel.onSiguienteClickPaso1()) onNext() }
                // onBack = null porque es el primer paso
            )
        }
    }
}

// Paso 2 del wizard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroPaso2(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegistroViewModel
) {
    RegistroLayout(2, "Información personal", R.drawable.figura_ondas_azul) {

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            // Nombre
            RequiredLabel("Nombre")
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Ingresa tu nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Apellido paterno
            RequiredLabel("Apellido paterno")
            OutlinedTextField(
                value = viewModel.apellidoPaterno,
                onValueChange = { viewModel.onApellidoPaternoChange(it) },
                label = { Text("Ingresa tu apellido paterno") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Apellido materno
            Text("Apellido materno")
            OutlinedTextField(
                value = viewModel.apellidoMaterno ?: "",
                onValueChange = { viewModel.onApellidoMaternoChange(it) },
                label = { Text("Ingresa tu apellido materno") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Ocupación
            RequiredLabel("Ocupación")
            OutlinedTextField(
                value = viewModel.ocupacion,
                onValueChange = { viewModel.onOcupacionChange(it) },
                label = { Text("Ingresa tu ocupación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Género
            RequiredLabel("Género")
            var generoExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = it }
            ) {
                OutlinedTextField(
                    value = viewModel.genero?.label?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona tu género") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    viewModel.generosOpciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.label) },
                            onClick = {
                                viewModel.onGeneroChange(opcion)
                                generoExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Fecha de nacimiento
            RequiredLabel("Fecha de nacimiento")
            var mostrarDatePicker by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = viewModel.fechaNacimiento?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona una fecha") },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

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
                                viewModel.onFechaNacimientoChange(fecha)
                            }
                            mostrarDatePicker = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(Modifier.height(20.dp))

            NavegacionPasos(
                onBack = onBack,
                onNext = { if (viewModel.onSiguienteClickPaso2()) onNext() }
            )

        }
    }
}

// Paso 3 del wizard
@Composable
fun RegistroPaso3(
    onNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegistroViewModel
) {
    RegistroLayout(3, "Información de la cuenta", R.drawable.figura_ondas_naranja) {

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            // Usuario
            RequiredLabel("Usuario")
            OutlinedTextField(
                value = viewModel.nickname,
                onValueChange = { viewModel.onNicknameChange(it) },
                label = { Text("Ingresa un nombre de usuario") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Foto de perfil
            Text("Foto de perfil")
            FotoPerfilPicker(
                fotoUri = viewModel.fotoUri,
                onFotoSeleccionada = { viewModel.onFotoChange(it) },
                onFotoTomada = { viewModel.onFotoChange(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            NavegacionPasos(
                onNext = { if (viewModel.onSiguienteClickPaso3()) onNext() },
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistroPaso4(
    onBack: () -> Unit,
    viewModel: RegistroViewModel
) {
    RegistroLayout(4, "Tus intereses", R.drawable.figura_ondas_lila) {

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            Text(text = "¿Qué te gusta hacer en tu tiempo libre? Elige tus intereses principales:")

            Spacer(Modifier.height(20.dp))

            // Cargar los chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Interes.entries.forEach { interes ->
                    val seleccionado = interes in viewModel.interesesSeleccionados

                    FilterChip(
                        selected = seleccionado,
                        onClick = { viewModel.onInteresToggle(interes) },
                        label = { Text(interes.label) },
                        leadingIcon = if (seleccionado) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    NavegacionPasos(onBack = onBack)
                }
                BotonPrincipal(
                    text = "Crear cuenta",
                    onClick = { if (viewModel.onFinishClickPaso4()) viewModel.registrarUsuario() }
                )
            }
        }
    }
}

@Composable
private fun RowReglaContraseña(texto: String, cumple: Boolean) {
    Row(modifier = Modifier
        .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (cumple) Green else GrayEnabled,
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = texto,
            fontSize = 13.sp,
            color = GrayEnabled
        )
    }
}

@Composable
private fun NavegacionPasos(
    onNext: (() -> Unit)? = null, // null = no muestra el botón
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            TextButton(
                onClick = { onBack() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Black
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Atrás",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(Modifier.width(1.dp)) // mantiene el siguiente a la derecha
        }

        if (onNext != null) {
            TextButton(
                onClick = { onNext() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Black
                )
            ) {
                Text(
                    text = "Siguiente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun FotoPerfilPicker(
    fotoUri: Uri?,
    onFotoSeleccionada: (Uri) -> Unit,
    onFotoTomada: (Uri) -> Unit
) {
    val context = LocalContext.current

    // Launcher para galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onFotoSeleccionada(it) } }

    // Uri temporal para la cámara
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para cámara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) cameraUri?.let { onFotoTomada(it) } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Círculo con foto o ícono
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GrayAlt)
        ) {
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = GrayEnabled,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botones
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Galería")
            }

            OutlinedButton(onClick = {
                val uri = context.createTempImageUri()
                cameraUri = uri
                camaraLauncher.launch(uri)
            }) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Cámara")
            }
        }
    }
}

// Extension para crear URI temporal para la cámara
fun Context.createTempImageUri(): Uri {
    val file = File(cacheDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "${packageName}.provider", file)
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun RegistroPaso3Preview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        RegistroPaso4({}, RegistroViewModel(UsuarioRepository(AppDatabase.getDatabase(LocalContext.current).usuarioDao())))
    }
}