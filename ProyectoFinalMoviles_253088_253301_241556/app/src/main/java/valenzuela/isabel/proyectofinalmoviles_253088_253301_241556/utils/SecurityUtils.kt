package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils

import org.mindrot.jbcrypt.BCrypt

object SecurityUtils {

    fun hashPassword(pass: String): String {
        return BCrypt.hashpw(pass, BCrypt.gensalt())
    }

    fun checkPassword(pass: String, passHashed: String): Boolean {
        return try {
            BCrypt.checkpw(pass, passHashed)
        } catch (e: Exception) {
            false
        }
    }
}