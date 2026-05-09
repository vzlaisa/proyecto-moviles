package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioConIntereses
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef

@Dao
interface UsuarioDAO {

    @Transaction
    @Query("SELECT * FROM usuarios")
    fun getAll(): Flow<List<UsuarioConIntereses>>

    @Transaction
    @Query("SELECT * FROM usuarios WHERE correo = :identificador OR nickname = :identificador LIMIT 1")
    suspend fun getByIdentificador(identificador: String): UsuarioConIntereses?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    /**
     * Solo se usa una vez (al iniciar la app para llenar los 10 intereses maestros. No se usa en el registro de usuario.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntereses(intereses: List<InteresEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<UsuarioInteresCrossRef>)

    @Query("SELECT foto_perfil FROM usuarios WHERE nickname = :nickname LIMIT 1")
    fun getImagenByNickname(nickname: String): Flow<String?>

    @Query("UPDATE usuarios SET contrasenia = :contrasenia WHERE correo = :correo")
    suspend fun updateContrasenia(correo: String, contrasenia: String)

    @Query("UPDATE usuarios SET huella_activa = :value WHERE nickname = :nickname")
    suspend fun updateHuellaActiva(nickname: String, value: Boolean)

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Transaction
    @Query("DELETE FROM usuario_interes WHERE idUsuario = :usuarioId")
    suspend fun deleteInteresesByUsuarioId(usuarioId: Int)

    @Query("UPDATE usuarios SET es_primer_login = 0 WHERE id = :id")
    suspend fun updateEsPrimerLogin(id: Int)
}