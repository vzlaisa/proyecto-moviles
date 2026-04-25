package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UsuarioConIntereses(
    @Embedded
    val usuario: UsuarioEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UsuarioInteresCrossRef::class,
            parentColumn = "idUsuario",
            entityColumn = "idInteres"
        )
    )
    val intereses: List<InteresEntity>
)