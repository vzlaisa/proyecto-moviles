package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.AppDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.BotonPrincipal
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.FondoOndulado
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredLabel
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.RequiredTextField
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayEnabled
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Green
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.CambiarContraViewModel

@Composable
fun ActualizarContraScreen(
    viewModel: CambiarContraViewModel
) {
    FondoOndulado(rutaImagen = R.drawable.figura_ondas_azul) {
        CardFondo {
            Column(
                modifier = Modifier.padding(40.dp)
            ) {
                Text(
                    text = "Restablece tu contraseña",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 23.sp)
                )

                Spacer(Modifier.height(20.dp))

                Text(text = "La contraseña debe de ser diferente a la anterior")

                Spacer(Modifier.height(40.dp))

                RequiredLabel("Nueva contraseña")
                RequiredTextField(
                    value = viewModel.nuevaPass,
                    onValueChange = { viewModel.onNuevaPassChange(it) },
                    placeholder = "Ingresa tu nueva contraseña",
                    error = viewModel.nuevaPassError
                )

                Spacer(Modifier.height(20.dp))

                RequiredLabel("Confirmar nueva contraseña")
                RequiredTextField(
                    value = viewModel.confirmarNuevaPass,
                    onValueChange = { viewModel.onConfirmarNuevaPassChange(it) },
                    placeholder = "Confirma tu nueva contraseña",
                    error = viewModel.confirmarNuevaPassError
                )

                Spacer(Modifier.height(20.dp))

                // Reglas de contraseña
                viewModel.reglas.forEach { (texto, cumple) ->
                    RowReglaContraseña(texto = texto, cumple = cumple)
                }

                Spacer(Modifier.weight(1f))

                // Botón verificar correo
                BotonPrincipal(
                    text = "Restablecer contraseña",
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth()
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ActualizarContraScreenPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        ActualizarContraScreen(CambiarContraViewModel(UsuarioRepository(AppDatabase.getDatabase(LocalContext.current).usuarioDao())))
    }
}