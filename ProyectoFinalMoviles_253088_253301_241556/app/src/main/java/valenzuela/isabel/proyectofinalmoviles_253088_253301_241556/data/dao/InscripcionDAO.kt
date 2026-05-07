package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion

@Dao
interface InscripcionDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(inscripcion: InscripcionEntity)

    @Query("""
        SELECT * FROM inscripciones 
        WHERE id_actividad = :idActividad AND id_usuario = :idUsuario
        LIMIT 1
    """)
    suspend fun getInscripcion(idActividad: Int, idUsuario: Int): InscripcionEntity

    @Query("""
        SELECT * FROM inscripciones 
        WHERE id_actividad = :idActividad AND estado != 'CANCELADO'
    """)
    fun getParticipantesActivos(idActividad: Int): Flow<List<InscripcionEntity>>

    @Query("""
        UPDATE inscripciones SET estado = :estado
        WHERE id_actividad = :idActividad AND id_usuario = :idUsuario
    """)
    suspend fun actualizarEstado(idActividad: Int, idUsuario: Int, estado: EstadoInscripcion)

    @Query("DELETE FROM inscripciones WHERE id_actividad = :idActividad AND id_usuario = :idUsuario")
    suspend fun eliminar(idActividad: Int, idUsuario: Int)

}