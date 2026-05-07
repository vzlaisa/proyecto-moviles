package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.EstadoInscripcion
import java.time.LocalDateTime

@Entity(
    tableName = "inscripciones",
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_actividad"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id_actividad"),
        Index("id_usuario"),
        Index(value = ["id_actividad", "id_usuario"], unique = true)
    ]
)
data class InscripcionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_actividad")
    val idActividad: Int,

    @ColumnInfo(name = "estado")
    val estado: EstadoInscripcion = EstadoInscripcion.CONFIRMADO,

    @ColumnInfo(name = "fecha_inscripcion")
    val fechaInscripcion: LocalDateTime = LocalDateTime.now()

)