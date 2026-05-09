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
import java.io.File
import java.io.FileOutputStream

class UsuarioRepository(private val usuarioDAO: UsuarioDAO) {

    suspend fun login(identificador: String, contrasenia: String): UsuarioConIntereses? {
        val usuarioObtenido = usuarioDAO.getByIdentificador(identificador)?: return null

        // Comparar contraseñas
        val contraseniaCorrecta = SecurityUtils.checkPassword(
            pass = contrasenia,
            passHashed = usuarioObtenido.usuario.contrasenia
        )

        return if (contraseniaCorrecta) usuarioObtenido else null
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
        val usuario = usuarioDAO.getByIdentificador(correo)

        return usuario != null
    }

    suspend fun nicknameYaExiste(nickname: String): Boolean {
        val usuario = usuarioDAO.getByIdentificador(nickname)

        return usuario != null
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

    suspend fun guardarImagenPerfil(bitmap: Bitmap, nickname: String, internalDir: File): String {
        val fileName = "profile_${nickname}_${System.currentTimeMillis()}.jpg"
        val file = File(internalDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        return file.absolutePath
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
        } catch (e: Exception) {
            Log.e("REPOSITORY_ERROR", "Error al actualizar perfil: ${e.message}")
            throw DatabaseException(e)
        }
    }

    // Esta es la clave para que el diálogo de huella solo salga una vez
    suspend fun marcarPrimerLoginCompletado(id: Int) {
        usuarioDAO.updateEsPrimerLogin(id)
    }
}