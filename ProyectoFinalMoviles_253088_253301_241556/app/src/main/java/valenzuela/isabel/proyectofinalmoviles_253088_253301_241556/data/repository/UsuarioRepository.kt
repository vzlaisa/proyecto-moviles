package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.DatabaseException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.UsuarioYaExisteException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils.SecurityUtils
import java.time.LocalDate
import kotlin.collections.map
import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class UsuarioRepository(private val usuarioDAO: UsuarioDAO) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(identificador: String, contrasenia: String): UsuarioConIntereses? {
        var usuarioObtenido = usuarioDAO.getByIdentificador(identificador)

        // Si no está local, intentar desde la nube (requiere red)
        if (usuarioObtenido == null) {
            usuarioObtenido = buscarYDescargarUsuarioDesdeNube(identificador)
        }

        if (usuarioObtenido == null) return null

        // Comparar contraseñas
        val contraseniaCorrecta = SecurityUtils.checkPassword(
            pass = contrasenia,
            passHashed = usuarioObtenido.usuario.contrasenia
        )

        if (!contraseniaCorrecta) return null

        // Subir a firestore si no existe (usuarios que se registraron sin red)
        sincronizarUsuarioANube(usuarioObtenido)

        return usuarioObtenido
    }

    suspend fun guardarImagenNueva(bitmap: Bitmap, nickname: String, internalDir: File): String {
        val fileName = "profile_${nickname}_${System.currentTimeMillis()}.jpg"
        val file = File(internalDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        return file.absolutePath
    }

    suspend fun getByIdentificador(identificador: String): UsuarioConIntereses? {
        return try {
            usuarioDAO.getByIdentificador(identificador)
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al obtener usuario por identificador: ${e.message}")
            null
        }
    }

    fun getFotoPerfil(nickname: String): Flow<String?> {
        return usuarioDAO.getImagenByNickname(nickname)
    }

    suspend fun correoYaExiste(correo: String): Boolean {
        // Primero local
        if (usuarioDAO.getByIdentificador(correo) != null) return true

        // Si no está local, buscar en la nube
        return try {
            val result = firestore.collection("usuarios")
                .whereEqualTo("correo", correo)
                .get().await()
            !result.isEmpty
        } catch (e: Exception) {
            false // Sin red, asumir que no existe
        }
    }


    suspend fun nicknameYaExiste(nickname: String): Boolean {
        if (usuarioDAO.getByIdentificador(nickname) != null) return true

        return try {
            val result = firestore.collection("usuarios")
                .whereEqualTo("nickname", nickname)
                .get().await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registrar(usuario: UsuarioEntity, intereses: List<Interes>) {
        // Validaciones de paso 1
        if (usuario.correo.isBlank()) throw ValidationException("El correo es obligatorio")
        if (usuario.contrasenia.isBlank()) throw ValidationException("La contraseña es obligatoria")

        // Validaciones de paso 2
        if (usuario.nombre.isBlank()) throw ValidationException("El nombre es obligatorio")
        if (usuario.apellidoPaterno.isBlank()) throw ValidationException("El apellido paterno es obligatorio")
        if (usuario.ocupacion.isBlank()) throw ValidationException("La ocupación es obligatoria")

        // Validación lógica de fecha
        if (usuario.fechaNacimiento.isAfter(LocalDate.now())) {
            throw ValidationException("La fecha de nacimiento no puede ser futura")
        }

        // Validaciones de paso 3
        if (usuario.nickname.isBlank()) throw ValidationException("El nombre de usuario es obligatorio")
        if (usuario.nickname.length < 3) throw ValidationException("El usuario debe tener al menos 3 caracteres")

        // Validaciones de paso 4
        if (intereses.isEmpty()) throw ValidationException("Debes seleccionar al menos un interés")

        try {
            // Hashear contraseña
            val usuarioConHash = usuario.copy(contrasenia = SecurityUtils.hashPassword(usuario.contrasenia))

            // Insertar usuario y recuperar el id
            val idUsuario = usuarioDAO.insertUsuario(usuarioConHash).toInt()

            // Crear la relación en la tabla intermedia (CrossRef)
            val crossRefs = intereses.map { interesEnum ->
                UsuarioInteresCrossRef(
                    idUsuario = idUsuario,
                    idInteres = interesEnum.ordinal + 1
                )
            }

            // Insertar las relaciones
            usuarioDAO.insertCrossRefs(crossRefs)

            try {
                firestore.collection("usuarios")
                    .document(usuario.nickname)
                    .set(hashMapOf(
                        "id" to idUsuario,
                        "nombre" to usuario.nombre,
                        "apellidoPaterno" to usuario.apellidoPaterno,
                        "apellidoMaterno" to usuario.apellidoMaterno,
                        "nickname" to usuario.nickname,
                        "correo" to usuario.correo,
                        "contrasenia" to usuarioConHash.contrasenia,
                        "genero" to usuario.genero.name,
                        "ocupacion" to usuario.ocupacion,
                        "fechaNacimiento" to usuario.fechaNacimiento.toString(),
                        "fotoPerfil" to usuario.fotoPerfil,
                        "huellaActiva" to usuario.huellaActiva,
                        "fechaRegistro" to usuario.fechaRegistro.toString(),
                        "esPrimerLogin" to usuario.esPrimerLogin,
                        "intereses" to intereses.map { it.name }
                    )).await()
            } catch (e: Exception) {
                Log.w("SYNC", "Sin red al registrar usuario, se sincronizará después: ${e.message}")
            }
        } catch (e: SQLiteConstraintException) {
            Log.e("REPOSITORY_ERROR", "Error al registrar usuario: ${e.message}")
            // Correo único duplicado
            throw UsuarioYaExisteException()
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error inesperado: ${e.message}")
            // Cualquier otro error de la base
            throw DatabaseException(e)
        }
    }

    suspend fun actualizarContrasenia(correo: String, contrasenia: String) {
        if (correo.isBlank()) {
            throw ValidationException("El correo es obligatorio")
        }

        if (contrasenia.isEmpty()) {
            throw ValidationException("La contraseña es obligatoria")
        }

        try {
            val usuario = usuarioDAO.getByIdentificador(correo) ?: throw ValidationException("No existe un usuario con ese correo")

            val contraActualHash = usuario.usuario.contrasenia

            if (SecurityUtils.checkPassword(contrasenia, contraActualHash)) throw ValidationException("La nueva contraseña debe de ser diferente a la anterior")

            val nuevaContraHasheada = SecurityUtils.hashPassword(contrasenia)

            usuarioDAO.updateContrasenia(usuario.usuario.correo, nuevaContraHasheada)

            try {
                firestore.collection("usuarios")
                    .whereEqualTo("correo", correo)
                    .get().await()
                    .documents.firstOrNull()
                    ?.reference
                    ?.update("contrasenia", nuevaContraHasheada)
                    ?.await()
            } catch (e: Exception) {
                Log.w("SYNC", "Sin red al actualizar contraseña, se sincronizará después: ${e.message}")
            }
        } catch (e: ValidationException) {
            throw e
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al actualizar contraseña: ${e.message}")
            throw DatabaseException(e)
        }
    }

    suspend fun actualizarHuellaActiva(nickname: String, value: Boolean) {
        if (nickname.isBlank()) {
            throw ValidationException("El nickname es obligatorio")
        }

        try {
            val usuario = usuarioDAO.getByIdentificador(nickname)
                ?: throw ValidationException("No existe un usuario con ese nickname")

            usuarioDAO.updateHuellaActiva(nickname, value)

            try {
                firestore.collection("usuarios")
                    .document(nickname)
                    .update("huellaActiva", value)
                    .await()
            } catch (e: Exception) {
                Log.w("SYNC", "Sin red al actualizar huella, se sincronizará después: ${e.message}")
            }
        } catch (e: ValidationException) {
            throw e
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al actualizar huella: ${e.message}")
            throw DatabaseException(e)
        }
    }

    @Transaction
    suspend fun actualizarPerfil(usuario: UsuarioEntity, nuevosIntereses: List<Interes>) {
        try {
            // Actualizar los datos del usuario
            usuarioDAO.updateUsuario(usuario)

            // Limpiar las relaciones viejas en la tabla intermedia
            usuarioDAO.deleteInteresesByUsuarioId(usuario.id)

            // Crear las nuevas relaciones usando los id de los intereses de la base
            val crossRefs = nuevosIntereses.map { interesEnum ->
                UsuarioInteresCrossRef(
                    idUsuario = usuario.id,
                    idInteres = interesEnum.ordinal + 1 // Mapeo directo al ID maestro
                )
            }

            // Insertar los nuevos puentes
            usuarioDAO.insertCrossRefs(crossRefs)

            try {
                firestore.collection("usuarios")
                    .document(usuario.nickname)
                    .update(mapOf(
                        "nombre" to usuario.nombre,
                        "apellidoPaterno" to usuario.apellidoPaterno,
                        "apellidoMaterno" to usuario.apellidoMaterno,
                        "genero" to usuario.genero.name,
                        "ocupacion" to usuario.ocupacion,
                        "fechaNacimiento" to usuario.fechaNacimiento.toString(),
                        "fotoPerfil" to usuario.fotoPerfil,
                        "intereses" to nuevosIntereses.map { it.name }
                    )).await()
            } catch (e: Exception) {
                Log.w("SYNC", "Sin red al actualizar perfil: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al actualizar perfil: ${e.message}")
            throw DatabaseException(e)
        }
    }

    suspend fun marcarPrimerLoginCompletado(id: Int) {
        usuarioDAO.updateEsPrimerLogin(id)
    }

    suspend fun obtenerCantidadActividadesCreadas(id: Int): Int {
        return try {
            usuarioDAO.getCantidadActividadesCreadas(id)
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al obtener conteo de actividades creadas de usuario: ${e.message}")
            throw DatabaseException(e)
        }
    }

    suspend fun obtenerCantidadActividadesUnidas(id: Int): Int {
        return try {
            usuarioDAO.getCantidadActividadesUnidas(id)
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al obtener conteo de actividades unidas de usuario: ${e.message}")
            throw DatabaseException(e)
        }
    }

    private suspend fun buscarYDescargarUsuarioDesdeNube(identificador: String): UsuarioConIntereses? {
        return try {
            val querySnapshot = if (identificador.contains("@")) {
                firestore.collection("usuarios").whereEqualTo("correo", identificador).get().await()
            } else {
                firestore.collection("usuarios").whereEqualTo("nickname", identificador).get().await()
            }

            if (querySnapshot.isEmpty) return null
            val document = querySnapshot.documents.first()

            val idNube = document.getLong("id")?.toInt() ?: return null
            val nicknameNube = document.getString("nickname") ?: document.id

            val usuarioEntity = UsuarioEntity(
                id = idNube,
                nombre = document.getString("nombre") ?: "",
                apellidoPaterno = document.getString("apellidoPaterno") ?: "",
                apellidoMaterno = document.getString("apellidoMaterno"),
                nickname = nicknameNube,
                correo = document.getString("correo") ?: "",
                contrasenia = document.getString("contrasenia") ?: "",
                genero = Genero.valueOf(document.getString("genero") ?: "OTRO"),
                ocupacion = document.getString("ocupacion") ?: "",
                fechaNacimiento = LocalDate.parse(document.getString("fechaNacimiento")),
                fotoPerfil = document.getString("fotoPerfil"),
                huellaActiva = document.getBoolean("huellaActiva") ?: false,
                fechaRegistro = LocalDateTime.parse(document.getString("fechaRegistro")),
                esPrimerLogin = document.getBoolean("esPrimerLogin") ?: true
            )

            usuarioDAO.insertUsuario(usuarioEntity)

            val interesesStr = document.get("intereses") as? List<String> ?: emptyList()
            val crossRefs = interesesStr.map {
                UsuarioInteresCrossRef(idUsuario = idNube, idInteres = Interes.valueOf(it).ordinal + 1)
            }
            usuarioDAO.insertCrossRefs(crossRefs)

            usuarioDAO.getByIdentificador(nicknameNube)
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al jalar datos de la nube en login: ${e.message}")
            null
        }
    }

    // Sube el usuario a firestore si no estaba, se llama en el login
    private suspend fun sincronizarUsuarioANube(usuarioConIntereses: UsuarioConIntereses) {
        try {
            val usuario = usuarioConIntereses.usuario
            val intereses = usuarioConIntereses.intereses

            firestore.collection("usuarios")
                .document(usuario.nickname)
                .set(hashMapOf(
                    "id" to usuario.id,
                    "nombre" to usuario.nombre,
                    "apellidoPaterno" to usuario.apellidoPaterno,
                    "apellidoMaterno" to usuario.apellidoMaterno,
                    "nickname" to usuario.nickname,
                    "correo" to usuario.correo,
                    "contrasenia" to usuario.contrasenia,
                    "genero" to usuario.genero.name,
                    "ocupacion" to usuario.ocupacion,
                    "fechaNacimiento" to usuario.fechaNacimiento.toString(),
                    "fotoPerfil" to usuario.fotoPerfil,
                    "huellaActiva" to usuario.huellaActiva,
                    "fechaRegistro" to usuario.fechaRegistro.toString(),
                    "esPrimerLogin" to usuario.esPrimerLogin,
                    "intereses" to intereses.map { it.nombre.name }
                )).await()

            Log.d("SYNC", "Usuario ${usuario.nickname} sincronizado a Firestore")
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al sincronizar usuario en login: ${e.message}")
        }
    }
}