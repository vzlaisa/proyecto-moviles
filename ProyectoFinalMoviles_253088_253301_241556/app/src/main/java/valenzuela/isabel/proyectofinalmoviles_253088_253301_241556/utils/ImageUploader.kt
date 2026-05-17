package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

object ImageUploader {

    private const val CLOUD_NAME = "dtrclbtyi"
    private const val UPLOAD_PRESET = "joinly_actividades"

    private const val MAX_LADO_PX = 1080
    private const val CALIDAD_JPEG = 85

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    suspend fun subirFotoActividad(
        context: Context,
        uri: Uri,
        idCreador: Int
    ): String = withContext(Dispatchers.IO) {
        val bytes = comprimirImagen(context, uri)
        val nombreArchivo = "${idCreador}_${System.currentTimeMillis()}_${UUID.randomUUID()}"

        val cuerpo = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "$nombreArchivo.jpg",
                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .addFormDataPart("folder", "actividades/$idCreador")
            .addFormDataPart("public_id", nombreArchivo)
            .build()

        val peticion = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(cuerpo)
            .build()

        httpClient.newCall(peticion).execute().use { respuesta ->
            val cuerpoRespuesta = respuesta.body?.string()
                ?: throw IllegalStateException("Respuesta vacía de Cloudinary")

            if (!respuesta.isSuccessful) {
                throw IllegalStateException(
                    "Cloudinary respondió ${respuesta.code}: $cuerpoRespuesta"
                )
            }

            val url = JSONObject(cuerpoRespuesta).optString("secure_url")
            if (url.isBlank()) {
                throw IllegalStateException("Cloudinary no devolvió secure_url")
            }
            url
        }
    }

    private fun comprimirImagen(context: Context, uri: Uri): ByteArray {
        val bitmapOriginal = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: throw IllegalStateException("No se pudo leer la imagen seleccionada")

        val bitmapRedimensionado = redimensionar(bitmapOriginal, MAX_LADO_PX)

        val baos = ByteArrayOutputStream()
        bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, baos)

        if (bitmapRedimensionado != bitmapOriginal) bitmapOriginal.recycle()
        bitmapRedimensionado.recycle()

        return baos.toByteArray()
    }

    private fun redimensionar(bitmap: Bitmap, maxLado: Int): Bitmap {
        val ancho = bitmap.width
        val alto = bitmap.height
        if (ancho <= maxLado && alto <= maxLado) return bitmap

        val nuevoAncho: Int
        val nuevoAlto: Int
        if (ancho >= alto) {
            nuevoAncho = maxLado
            nuevoAlto = (alto.toFloat() / ancho.toFloat() * maxLado).toInt()
        } else {
            nuevoAlto = maxLado
            nuevoAncho = (ancho.toFloat() / alto.toFloat() * maxLado).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
    }
}