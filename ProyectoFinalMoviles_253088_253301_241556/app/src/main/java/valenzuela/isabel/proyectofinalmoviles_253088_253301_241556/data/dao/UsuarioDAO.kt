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

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getByCorreo(correo: String): UsuarioConIntereses?

    @Query("SELECT * FROM usuarios WHERE nickname = :nickname LIMIT 1")
    suspend fun  getByNickname(nickname: String): UsuarioConIntereses?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntereses(intereses: List<InteresEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<UsuarioInteresCrossRef>)

    @Query("UPDATE usuarios SET contrasenia = :contrasenia WHERE correo = :correo")
    suspend fun updateContrasenia(correo: String, contrasenia: String)
}