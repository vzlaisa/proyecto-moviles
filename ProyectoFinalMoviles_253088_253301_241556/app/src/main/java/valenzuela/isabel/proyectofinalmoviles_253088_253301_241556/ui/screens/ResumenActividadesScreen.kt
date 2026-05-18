package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.R
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresConteo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NuevaPersona
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components.CardFondo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BeigeAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.BlueAlt
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.OrangePrimary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.PinkSecondary
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.ProyectoFinalMoviles_253088_253301_241556Theme
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.theme.White
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel.ResumenViewModel

@Composable
fun ResumenActividadesScreen(
    viewModel: ResumenViewModel
) {
    val topIntereses by viewModel.topIntereses.collectAsState()
    val nuevasPersonas by viewModel.nuevasPersonas.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_resumenactividades),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Spacer(Modifier.height(60.dp))
            Text(
                text = "Resumen mensual",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        CardFondo {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                item {
                    Text(
                        text = "¡Mira tu mes en (J)oinly!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item { Spacer(Modifier.height(10.dp)) }

                // Card de actividades
                item { CardActividades(topIntereses) }

                // Card de circulo
                item { CardCirculo(nuevasPersonas) }
            }
        }
    }
}

@Composable
private fun CardActividades(
    topIntereses: List<InteresConteo>
) {
    val colores = listOf(
        BlueAlt,
        OrangePrimary,
        BeigeAlt
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PinkSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actividades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val mensaje = when {
                topIntereses.isEmpty() -> "No te has unido a actividades este mes"
                topIntereses.size == 1 -> "¡Tu interés favorito este mes!"
                else -> "¡Has tenido muchas actividades este mes!"
            }

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            if (topIntereses.isEmpty()) {
                Text(
                    text = "Únete a actividades para ver tu resumen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    topIntereses.forEachIndexed { index, interes ->
                        val alineacion = when (index) {
                            0 -> Alignment.TopCenter
                            1 -> Alignment.BottomStart
                            else -> Alignment.BottomEnd
                        }
                        val paddingModifier = when (index) {
                            1 -> Modifier.padding(start = 20.dp)
                            2 -> Modifier.padding(end = 20.dp)
                            else -> Modifier
                        }

                        CirculoInteres(
                            numero = "#${index + 1}",
                            label = interes.nombre.label,
                            color = colores[index],
                            modifier = Modifier
                                .align(alineacion)
                                .then(paddingModifier)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CirculoInteres(
    numero: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = numero,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CardCirculo(
    nuevasPersonas: List<NuevaPersona>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BlueAlt)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally  // <- esto faltaba
        ) {
            Text(
                text = "Tu círculo crece...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (nuevasPersonas.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Únete a actividades para conocer personas nuevas",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            } else {
                Text(
                    text = "+${nuevasPersonas.size} personas nuevas!",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                AvatarsRow(personas = nuevasPersonas)

                Spacer(Modifier.height(12.dp))

                val nombresTexto = when {
                    nuevasPersonas.size == 1 ->
                        "Conociste a ${nuevasPersonas[0].nombre} este mes"
                    nuevasPersonas.size == 2 ->
                        "Conociste a ${nuevasPersonas[0].nombre} y ${nuevasPersonas[1].nombre}"
                    else -> {
                        val primeros = nuevasPersonas.take(2).joinToString(" y ") { it.nombre }
                        val restantes = nuevasPersonas.size - 2
                        "Conociste a $primeros y $restantes más"
                    }
                }

                Text(
                    text = nombresTexto,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun AvatarsRow(
    personas: List<NuevaPersona>
) {
    val visibles = personas.take(5)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        visibles.forEachIndexed { index, persona ->
            Box(
                modifier = Modifier
                    .offset(x = ((index - visibles.size / 2) * 40).dp)
                    .size(60.dp)
                    .background(Color.LightGray, CircleShape)
                    .border(2.dp, White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = persona.fotoPerfil?: R.drawable.default_profile_pic,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenActividadesPreview() {
    ProyectoFinalMoviles_253088_253301_241556Theme {
        //ResumenActividadesScreen()
    }
}