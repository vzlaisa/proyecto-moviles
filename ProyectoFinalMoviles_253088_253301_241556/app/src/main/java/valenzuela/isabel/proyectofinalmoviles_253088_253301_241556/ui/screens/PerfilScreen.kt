package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BeigeAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.Black
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.GrayAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.DateUtils.toLongString
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.DateUtils.toMonthYearString
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.PerfilViewModel
import java.time.LocalDate

@Composable
fun PerfilScreen(viewModel: PerfilViewModel) {
    val datosCompletos = viewModel.usuario.collectAsStateWithLifecycle()

    if (datosCompletos.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        PerfilContent(datos = datosCompletos.value!!)
    }
}

@Composable
private fun PerfilContent(datos: UsuarioConIntereses) {
    val usuario = datos.usuario
    val intereses = datos.intereses

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mi información", "Mis intereses")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            // Portada
            Image(
                painter = painterResource(R.drawable.portada_perfil_usuario),
                contentDescription = "Portada",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            // Foto de perfil
            AsyncImage(
                model = usuario.fotoPerfil ?: R.drawable.default_profile_pic,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Nombre de usuario
            Text(
                text = usuario.nickname,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(30.dp))

            // falta cambiar para usar view model
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "2",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Actividades creadas",
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "5",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Actividades que se unió",
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para editar perfil
                Button(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(text = "Editar perfil")
                }

                // Botón para configuración
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tabs de información
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    contentColor = OrangePrimary, // Color del texto seleccionado
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = GrayAlt // Color de la línea de abajo
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) OrangePrimary else Black,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Contenido que cambia según la pestaña
                when (selectedTabIndex) {
                    0 -> InformacionTab(usuario)
                    1 -> InteresesTab(intereses)
                }
            }
        }
    }
}

@Composable
private fun InformacionTab(usuario: UsuarioEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Card de información personal
        InfoCard(titulo = "Información personal") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                DatoPerfil("Nombre completo", usuario.nombreCompleto)
                DatoPerfil("Género", usuario.genero.label)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                DatoPerfil("Fecha de nacimiento", usuario.fechaNacimiento.toLongString())
                DatoPerfil("Edad", "${usuario.edad} años")
            }
            DatoPerfil("Ocupación", usuario.ocupacion)
        }

        Spacer(Modifier.height(15.dp))

        // Card de información de la cuenta
        InfoCard(titulo = "Información de la cuenta") {
            DatoPerfil("Correo electrónico", usuario.correo)
            DatoPerfil("Miembro desde", usuario.fechaRegistro.toMonthYearString())
        }
    }
}

@Composable
fun InteresesTab(intereses: List<InteresEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .padding(7.dp)
        ) {
            intereses.forEach { interes ->
                Text(
                    interes.nombre.label,
                    modifier = Modifier
                        .background(color = BlueAlt, shape = CircleShape)
                        .padding(vertical = 8.dp, horizontal = 15.dp)
                )
            }
        }
    }
}

@Composable
private fun DatoPerfil(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun InfoCard(
    titulo: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BeigeAlt),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = content
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        // Objeto falso para la vista previa
        val mockUsuario = UsuarioConIntereses(
            usuario = UsuarioEntity(
                nombre = "Isabel",
                apellidoPaterno = "Valenzuela",
                nickname = "isabel_v",
                correo = "isabel@ejemplo.com",
                contrasenia = "",
                genero = Genero.FEMENINO,
                ocupacion = "Estudiante",
                fechaNacimiento = LocalDate.now(),
                fotoPerfil = null
            ),
            intereses = listOf(
                InteresEntity(nombre = Interes.AIRE_LIBRE),
                InteresEntity(nombre = Interes.LITERATURA),
                InteresEntity(nombre = Interes.VIDEOJUEGOS),
                InteresEntity(nombre = Interes.ESTUDIO),
                InteresEntity(nombre = Interes.ARTE)
            )
        )

        PerfilContent(datos = mockUsuario)
    }
}