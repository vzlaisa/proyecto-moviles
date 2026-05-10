package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils

import android.content.Context
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {
    suspend fun getUbicacion(context: Context): Pair<Double, Double>? {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { cont ->
            try {
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(0)
                    .build()

                fusedClient.getCurrentLocation(request, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            cont.resume(Pair(location.latitude, location.longitude))
                        } else {
                            // fallback a lastLocation
                            fusedClient.lastLocation
                                .addOnSuccessListener { last ->
                                    cont.resume(if (last != null) Pair(last.latitude, last.longitude) else null)
                                }
                                .addOnFailureListener { cont.resume(null) }
                        }
                    }
                    .addOnFailureListener { cont.resume(null) }

            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }
    }

    // Fórmula para calcular distancia entre dos coordenadas
    fun calcularDistanciaKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val radioTierra = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radioTierra * c
    }
}