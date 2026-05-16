package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.TipoNotificacion
import java.time.LocalDateTime

@Entity(
    tableName = "notificaciones",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_actividad"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id_usuario"),
        Index("id_actividad")
    ]
)
data class NotificacionEntity(
    @PrimaryKey
    val firestoreId: String,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "mensaje")
    val mensaje: String,

    @ColumnInfo(name = "fecha_recibida")
    val fecha: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "leida")
    val leida: Boolean = false,

    @ColumnInfo(name = "tipo")
    val tipo: TipoNotificacion,

    @ColumnInfo(name = "id_actividad")
    val idActividad: Int? = null
)