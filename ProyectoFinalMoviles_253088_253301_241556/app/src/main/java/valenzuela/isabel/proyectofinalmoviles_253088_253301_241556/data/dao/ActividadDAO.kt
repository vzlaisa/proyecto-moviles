package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadConDetalle
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresConteo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NuevaPersona
import java.time.LocalDate

@Dao
interface ActividadDAO {

    @Transaction
    @Query("SELECT * FROM actividades ORDER BY fecha_hora ASC")
    fun getActividades(): Flow<List<ActividadConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM actividades
        WHERE (:idInteres IS NULL OR id_interes = :idInteres)
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

    @Update
    suspend fun updateActividad(actividad: ActividadEntity)

    @Query("DELETE FROM actividades")
    suspend fun limpiarActividadesLocales()

    @Query("SELECT COUNT(*) FROM actividades WHERE pendiente_sync = 1")
    fun getPendientesSyncCount(): Flow<Int>

    @Query("SELECT * FROM actividades WHERE pendiente_sync = 1")
    suspend fun getPendientes(): List<ActividadEntity>

    @Query("UPDATE actividades SET pendiente_sync = 0 WHERE id = :id")
    suspend fun marcarComoSincronizada(id: Int)

    @Query("UPDATE actividades SET pendiente_sync = 1 WHERE id = :id")
    suspend fun marcarComoPendiente(id: Int)

    @Query("""
        SELECT 
            i.nombre, 
            COUNT(*) as total
        FROM inscripciones ins
        INNER JOIN actividades a ON ins.id_actividad = a.id
        INNER JOIN intereses i ON a.id_interes = i.id
        WHERE ins.id_usuario = :idUsuario
        AND strftime('%Y-%m', a.fecha_hora) = strftime('%Y-%m', 'now')
        AND ins.estado IN ('CONFIRMADO', 'ASISTENCIA_CONFIRMADA')
        GROUP BY i.id
        ORDER BY total DESC
        LIMIT 3
    """)
    fun getTop3InteresesDelMes(idUsuario: Int): Flow<List<InteresConteo>>

    @Query("""
        SELECT DISTINCT u.id, u.nombre, u.apellido_paterno, u.foto_perfil
        FROM inscripciones ins
        INNER JOIN usuarios u ON ins.id_usuario = u.id
        WHERE ins.id_actividad IN (
            -- Actividades a las que se unió el usuario este mes
            SELECT id_actividad FROM inscripciones
            WHERE id_usuario = :idUsuario
            AND estado IN ('CONFIRMADO', 'ASISTENCIA_CONFIRMADA')
            AND strftime('%Y-%m', fecha_inscripcion) = strftime('%Y-%m', 'now')
        )
        AND ins.id_usuario != :idUsuario
        AND ins.estado IN ('CONFIRMADO', 'ASISTENCIA_CONFIRMADA')
        AND u.id NOT IN (
            -- Excluir personas que ya conocía en meses anteriores
            SELECT DISTINCT ins2.id_usuario
            FROM inscripciones ins2
            INNER JOIN inscripciones ins3 ON ins2.id_actividad = ins3.id_actividad
            WHERE ins3.id_usuario = :idUsuario
            AND strftime('%Y-%m', ins2.fecha_inscripcion) < strftime('%Y-%m', 'now')
        )
    """)
    fun getNuevasPersonasDelMes(idUsuario: Int): Flow<List<NuevaPersona>>

}