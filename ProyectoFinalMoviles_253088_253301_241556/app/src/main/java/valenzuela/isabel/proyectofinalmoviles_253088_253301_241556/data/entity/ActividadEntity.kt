package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "actividades",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCreador"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InteresEntity::class,
            parentColumns = ["id"],
            childColumns = ["idInteres"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("idCreador"),
        Index("idInteres")
    ]
)
data class ActividadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "fecha_hora")
    val fechaHora: LocalDateTime,

    @ColumnInfo(name = "fecha_limite")
    val fechaLimite: LocalDateTime?,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "ubicacion")
    val ubicacion: String,

    @ColumnInfo(name = "longitud")
    val longitud: Double,

    @ColumnInfo(name = "latitud")
    val latitud: Double,

    @ColumnInfo(name = "max_participantes")
    val maxParticipantes: Int,

    @ColumnInfo(name = "publica")
    val publica: Boolean = true,

    @ColumnInfo(name = "recurrente")
    val recurrente: Boolean = false,

    // Relaciones
    @ColumnInfo(name = "id_creador")
    val idCreador: Int,

    @ColumnInfo(name = "id_interes")
    val idInteres: Int

)