package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.DatabaseException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.UsuarioYaExisteException
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.exception.ValidationException
import kotlin.collections.map

class UsuarioRepository(private val usuarioDAO: UsuarioDAO) {

    suspend fun login(correo: String, contrasenia: String): UsuarioConIntereses? =
        usuarioDAO.login(correo, contrasenia)

    suspend fun registrar(usuario: UsuarioEntity, intereses: List<Interes>) {
        if (usuario.correo.isBlank()) {
            throw ValidationException("Correo obligatorio")
        }

        if (usuario.contrasenia.isBlank()) {
            throw ValidationException("Contraseña obligatoria")
        }

        if (intereses.isEmpty()) {
            throw ValidationException("Selecciona intereses")
        }

        try {
            // Insertar usuario y recuperar el id
            val idUsuario = usuarioDAO.insertUsuario(usuario).toInt()

            // Registrar los intereses en su tabla
            val interesEntities = intereses.map { InteresEntity(nombre = it) }
            val idsGenerados = usuarioDAO.insertIntereses(interesEntities)

            // Crear la relación en la tabla intermedia
            val crossRefs = idsGenerados.map {
                UsuarioInteresCrossRef(idUsuario = idUsuario, idInteres = it.toInt())
            }

            usuarioDAO.insertCrossRefs(crossRefs)
        } catch (e: SQLiteConstraintException) {
            Log.e("REPO_ERROR", "Error al registrar: ${e.message}")
            // Correo único duplicado
            throw UsuarioYaExisteException()
        } catch (e: Exception) {
            // Cualquier otro error de la base
            throw DatabaseException(e)
        }
    }
}