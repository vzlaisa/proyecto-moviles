package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import java.time.LocalDate

@Dao
interface ActividadDAO {

    @Transaction
    @Query("SELECT * FROM actividades ORDER BY fecha_hora ASC")
    fun getActividades(): Flow<List<ActividadConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM actividades
        WHERE (:idInteres IS NULL OR :idInteres = :idInteres)
        AND (:texto = '' OR nombre LIKE '%' || :texto || '%')
        AND (:fecha IS NULL OR date(fecha_hora) = date(:fecha))
        ORDER BY fecha_hora ASC
    """)
    fun getActividadesFiltradas(
        idInteres: Int?,
        texto: String,
        fecha: LocalDate?
    ): Flow<List<ActividadConDetalle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: ActividadEntity): Long

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun getById(id: Int): ActividadEntity

    @Delete
    suspend fun deleteActividad(actividad: ActividadEntity)

}