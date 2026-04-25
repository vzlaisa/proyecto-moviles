package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "usuario_interes",
    primaryKeys = ["idUsuario", "idInteres"],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["idUsuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InteresEntity::class,
            parentColumns = ["id"],
            childColumns = ["idInteres"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idUsuario"), Index("idInteres")]
)
data class UsuarioInteresCrossRef(
    val idUsuario: Int,
    val idInteres: Int
)