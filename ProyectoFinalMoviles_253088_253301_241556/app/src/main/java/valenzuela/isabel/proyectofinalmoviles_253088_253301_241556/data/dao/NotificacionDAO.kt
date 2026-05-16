package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NotificacionEntity

@Dao
interface NotificacionDAO {

    @Query("SELECT * FROM notificaciones WHERE id_usuario = :userId ORDER BY fecha_recibida DESC")
    fun getAllNotificaciones(userId: Int): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE id_usuario = :userId AND leida = 0")
    fun getContadorNoLeidas(userId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: NotificacionEntity)

    @Query("UPDATE notificaciones SET leida = 1 WHERE firestoreId = :firestoreId")
    suspend fun marcarComoLeida(firestoreId: String)

    @Query("UPDATE notificaciones SET leida = 1 WHERE id_usuario = :userId")
    suspend fun marcarTodasComoLeidas(userId: Int)
}