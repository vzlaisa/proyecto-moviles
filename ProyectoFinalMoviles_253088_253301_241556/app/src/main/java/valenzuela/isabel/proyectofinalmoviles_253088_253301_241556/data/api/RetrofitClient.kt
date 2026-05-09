package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // GsonConverterFactory convierte el JSON de la API a los objetos de Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val nominatimService: NominatimService by lazy {
        retrofit.create(NominatimService::class.java)
    }
}