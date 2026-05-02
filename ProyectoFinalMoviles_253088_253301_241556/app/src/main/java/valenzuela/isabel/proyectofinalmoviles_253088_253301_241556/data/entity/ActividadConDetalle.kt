package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ActividadConDetalle(

    @Embedded
    val actividad: ActividadEntity,

    @Relation(
        parentColumn = "idCreador",
        entityColumn = "id"
    )
    val creador: UsuarioEntity,

    @Relation(
        parentColumn = "idInteres",
        entityColumn = "id"
    )
    val interes: InteresEntity

)