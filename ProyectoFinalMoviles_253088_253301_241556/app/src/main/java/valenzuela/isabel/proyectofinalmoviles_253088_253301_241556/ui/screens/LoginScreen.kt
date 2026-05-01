package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.DataStoreManager
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.FondoOndulado
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredLabel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueLink
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    onCambiarCuenta: () -> Unit,
    onCambiarContra: () -> Unit,
    onRegistrarse: () -> Unit,
    viewModel: AuthViewModel
) {
    FondoOndulado(
        rutaImagen = R.drawable.figura_ondas_lila
    ) {
        var correo by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        val fotoPerfil by viewModel.fotoPerfil.collectAsState()
        val nickname by viewModel.nickname.collectAsState()

        val hasAccountState by viewModel.hasAccount.collectAsState()
        val initialHasAccount = remember { hasAccountState } // Es para que no se actualice la pantalla en automático

        CardFondo {
            Column(
                modifier = Modifier
                    .padding(40.dp)
            ) {
                if (initialHasAccount) {
                    Column(
                        modifier = Modifier.padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = fotoPerfil,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(15.dp))

                        Text(
                            text = "¡Hola de nuevo, $nickname!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            buildAnnotatedString {
                                append("¿No eres $nickname? ")

                                withStyle(
                                    SpanStyle(
                                        color = BlueLink,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Cambiar de cuenta")
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable { onCambiarCuenta() }
                        )

                        Spacer(Modifier.height(40.dp))
                    }
                } else {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(30.dp))

                    // Correo
                    RequiredLabel("Correo electrónico")
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Ej. correo@gmail.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    viewModel.correoError?.let { mensaje ->
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }

                // Contraseña
                RequiredLabel("Contraseña")
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Ingresa tu contraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                viewModel.passError?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = BlueLink,
                        modifier = Modifier.clickable { onCambiarContra() }
                    )
                }

                Spacer(Modifier.height(20.dp))

                viewModel.loginError?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(40.dp))

                // Botón para ingresar
                BotonPrincipal(
                    text = "Ingresar",
                    onClick = { viewModel.login(correo, pass)},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!initialHasAccount) {
                    // Link para registrarse
                    Text(
                        buildAnnotatedString {
                            append("¿No tienes una cuenta? ")

                            withStyle(
                                SpanStyle(
                                    color = BlueLink,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Registrarse")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onRegistrarse() }
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        LoginScreen({}, {}, {},  AuthViewModel(dataStore = DataStoreManager(LocalContext.current), repository = UsuarioRepository(AppDatabase.getDatabase(LocalContext.current).usuarioDao())))
    }
}