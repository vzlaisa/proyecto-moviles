package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class InscripcionConUsuario(
    @Embedded val inscripcion: InscripcionEntity,
    @Relation(
        parentColumn = "id_usuario",
        entityColumn = "id"
    )
    val usuario: UsuarioEntity
)