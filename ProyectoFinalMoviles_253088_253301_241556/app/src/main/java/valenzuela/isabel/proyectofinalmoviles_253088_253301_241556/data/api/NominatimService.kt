package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Interfaz para Retrofit
interface NominatimService {

    @Headers("User-Agent: JoinlyApp/1.0 (isabel.valenzuela253301@potros.itson.edu.mx)")
    @GET("search")
    suspend fun buscarLugar(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("addressdetails") addressDetails: Int = 1
    ): List<NominatimResponse>
}