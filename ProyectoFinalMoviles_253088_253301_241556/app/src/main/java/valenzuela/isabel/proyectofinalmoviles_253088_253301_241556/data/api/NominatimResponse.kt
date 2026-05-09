package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("display_name")
    val nombreFormateado: String,

    @SerializedName("lat")
    val latitud: String,

    @SerializedName("lon")
    val longitud: String
)