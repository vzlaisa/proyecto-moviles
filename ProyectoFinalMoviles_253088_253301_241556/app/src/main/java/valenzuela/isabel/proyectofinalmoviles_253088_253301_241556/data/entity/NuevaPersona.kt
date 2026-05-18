package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.ColumnInfo

data class NuevaPersona(
    val id: Int,
    val nombre: String,
    @ColumnInfo(name = "apellido_paterno")
    val apellidPaterno: String,
    @ColumnInfo(name = "foto_perfil") val fotoPerfil: String?
)