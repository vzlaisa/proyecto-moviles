package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasenia = :contrasenia LIMIT 1")
    suspend fun login(correo: String, contrasenia: String): UsuarioConIntereses?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntereses(intereses: List<InteresEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<UsuarioInteresCrossRef>)


}