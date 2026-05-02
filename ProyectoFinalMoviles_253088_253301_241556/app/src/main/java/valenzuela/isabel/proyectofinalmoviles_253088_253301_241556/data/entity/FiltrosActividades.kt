package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import java.time.LocalDate

data class FiltrosActividades(

    val idInteres: Int? = null,
    val textoBusqueda: String = "",
    val distanciaMax: Float? = null,
    val fecha: LocalDate? = null,
    val latUsuario: Double? = null,
    val lonUsuario: Double? = null,

)