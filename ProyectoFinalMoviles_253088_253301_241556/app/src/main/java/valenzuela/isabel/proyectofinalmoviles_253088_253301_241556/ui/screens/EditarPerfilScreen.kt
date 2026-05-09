package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.*
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.*
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.PerfilViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSubCategory by remember { mutableIntStateOf(1) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(viewModel.actualizacionExitosa) {
        if (viewModel.actualizacionExitosa) {
            viewModel.actualizacionExitosa = false
            onNavigateBack()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.fotoPerfilEdit = it.toString() }
        showBottomSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.fotoPerfilBitmap = it }
        showBottomSheet = false
    }

    FondoOndulado(rutaImagen = R.drawable.figura_ondas_azul, colorFondo = BeigeAlt) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                SmallTopAppBar(
                    title = "Editar perfil",
                    onBack = onNavigateBack,
                    actionContent = {
                        Button(
                            onClick = {
                                viewModel.guardarCambios(context.filesDir)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Guardar", color = White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding() + 20.dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(White)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Edita la información de tu perfil",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                TabRow(selectedTabIndex = selectedTab, containerColor = White, contentColor = OrangePrimary) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Mi información", modifier = Modifier.padding(16.dp), color = if(selectedTab == 0) OrangePrimary else GrayAlt)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Mis intereses", modifier = Modifier.padding(16.dp), color = if(selectedTab == 1) OrangePrimary else GrayAlt)
                    }
                }

                if (selectedTab == 0) {
                    Row(modifier = Modifier.padding(24.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip("Información de la cuenta", selectedSubCategory == 0) { selectedSubCategory = 0 }
                        CategoryChip("Información personal", selectedSubCategory == 1) { selectedSubCategory = 1 }
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        if (selectedSubCategory == 1) {
                            RequiredLabel("Nombre")
                            RequiredTextField(value = viewModel.nombreEdit, onValueChange = { viewModel.nombreEdit = it }, placeholder = "Nombre")

                            Spacer(Modifier.height(12.dp))
                            RequiredLabel("Apellido paterno")
                            RequiredTextField(value = viewModel.apellidoPaternoEdit, onValueChange = { viewModel.apellidoPaternoEdit = it }, placeholder = "Paterno")

                            Spacer(Modifier.height(12.dp))
                            Text("Apellido materno", color = Black, fontWeight = FontWeight.Medium)
                            RequiredTextField(value = viewModel.apellidoMaternoEdit, onValueChange = { viewModel.apellidoMaternoEdit = it }, placeholder = "Materno")

                            Spacer(Modifier.height(12.dp))
                            RequiredLabel("Ocupación")
                            RequiredTextField(value = viewModel.ocupacionEdit, onValueChange = { viewModel.ocupacionEdit = it }, placeholder = "Estudiante")

                            Spacer(Modifier.height(12.dp))
                            RequiredLabel("Género")
                            ExposedDropdownMenuBox(
                                expanded = expandedGender,
                                onExpandedChange = { expandedGender = !expandedGender }
                            ) {
                                OutlinedTextField(
                                    value = viewModel.generoEdit?.label ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                                )
                                ExposedDropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                                    Genero.entries.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.label) },
                                            onClick = {
                                                viewModel.generoEdit = option
                                                expandedGender = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            RequiredLabel("Fecha de nacimiento")
                            OutlinedTextField(
                                value = viewModel.fechaNacimientoEdit?.toString() ?: "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = Black,
                                    disabledBorderColor = GrayAlt,
                                    disabledTrailingIconColor = Black
                                ),
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null) }
                            )

                            Spacer(Modifier.height(20.dp))
                            Text("Foto de perfil", color = Black, fontWeight = FontWeight.Bold)
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.clickable { showBottomSheet = true }, contentAlignment = Alignment.BottomEnd) {
                                    val imageModel: Any = remember (viewModel.fotoPerfilBitmap, viewModel.fotoPerfilEdit) {
                                        viewModel.fotoPerfilBitmap ?: viewModel.fotoPerfilEdit ?: R.drawable.default_profile_pic
                                    }
                                    AsyncImage(
                                        model = imageModel,
                                        contentDescription = null,
                                        modifier = Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFE1BEE7)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(modifier = Modifier.size(32.dp).clip(CircleShape).border(1.dp, GrayAlt, CircleShape), color = White) {
                                        Icon(Icons.Default.Edit, null, modifier = Modifier.padding(6.dp), tint = Black)
                                    }
                                }
                            }
                        } else {
                            RequiredLabel("Correo electrónico")
                            RequiredTextField(value = viewModel.emailEdit, onValueChange = { viewModel.emailEdit = it }, placeholder = "correo@ejemplo.com")

                            Spacer(Modifier.height(16.dp))
                            RequiredLabel(" Nueva Contraseña")
                            OutlinedTextField(
                                value = viewModel.passwordEdit,
                                onValueChange = { viewModel.passwordEdit = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                    }
                                },
                                placeholder = { Text("Ingresa tu nueva contraseña") }
                            )

                            Spacer(Modifier.height(16.dp))
                            RequiredLabel("Confirmar Contraseña")
                            OutlinedTextField(
                                value = viewModel.confirmacionPasswordEdit,
                                onValueChange = { viewModel.confirmacionPasswordEdit = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                    }
                                },
                                placeholder = { Text("Repite tu nueva contraseña") }
                            )


                            Spacer(Modifier.height(16.dp))
                            PasswordRequirement("Al menos 8 caracteres", viewModel.passwordEdit.length >= 8)
                            PasswordRequirement("Al menos 1 número", viewModel.passwordEdit.any { it.isDigit() })
                            PasswordRequirement("Al menos 1 mayúscula", viewModel.passwordEdit.any { it.isUpperCase() })
                            PasswordRequirement("Las contraseñas coinciden", viewModel.passwordEdit.isNotBlank() && viewModel.passwordEdit == viewModel.confirmacionPasswordEdit)
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Actualiza tus intereses principales:", color = Black, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(20.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Interes.entries.forEach { interes ->
                                ChipInteractable(text = interes.label, selected = viewModel.interesesSeleccionados.contains(interes), onClick = { viewModel.toggleInteres(interes) })
                            }
                        }
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.fechaNacimientoEdit = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text("OK", color = OrangePrimary) }
                }
            ) { DatePicker(state = datePickerState) }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, containerColor = White) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 24.dp, end = 24.dp)) {
                    Text("Editar foto de perfil", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Black)
                    Spacer(Modifier.height(20.dp))

                    OptionRow(Icons.Default.Delete, "Eliminar foto", Color.Red) {
                        viewModel.fotoPerfilEdit = null
                        viewModel.fotoPerfilBitmap = null
                        showBottomSheet = false
                    }
                    OptionRow(Icons.Default.PhotoLibrary, "Escoger de la galería", Black) { galleryLauncher.launch("image/*") }
                    OptionRow(Icons.Default.PhotoCamera, "Tomar una foto", Black) { cameraLauncher.launch() }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) Color(0xFFFCE4EC) else White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (selected) Color(0xFFFCE4EC) else GrayAlt)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color(0xFFC2185B) else Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PasswordRequirement(text: String, isValid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isValid) Color(0xFF4CAF50) else GrayAlt,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = text, color = GrayAlt, fontSize = 12.sp)
    }
}

@Composable
fun OptionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color)
        Spacer(Modifier.width(15.dp))
        Text(label, color = color)
    }
}

@Composable
fun ChipInteractable(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFFE1BEE7) else White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF9575CD) else GrayAlt,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF512DA8) else Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}