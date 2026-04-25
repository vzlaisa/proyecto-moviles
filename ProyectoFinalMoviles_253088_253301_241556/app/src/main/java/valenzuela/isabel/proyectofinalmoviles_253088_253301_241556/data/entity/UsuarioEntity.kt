package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["correo"], unique = true),
        Index(value = ["nickname"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido_paterno")
    val apellidoPaterno: String,

    @ColumnInfo(name = "apellido_materno")
    val apellidoMaterno: String? = null, // para que sea opcional

    @ColumnInfo(name = "nickname")
    val nickname: String,

    @ColumnInfo(name = "correo")
    val correo: String,

    @ColumnInfo(name = "contrasenia")
    val contrasenia: String,

    @ColumnInfo(name = "genero")
    val genero: Genero,

    @ColumnInfo(name = "ocupacion")
    val ocupacion: String,

    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimiento: LocalDate,

    @ColumnInfo(name = "foto_perfil")
    val fotoPerfil: String? = null,

    @ColumnInfo(name = "huella_activa")
    val huellaActiva: Boolean = false,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: LocalDateTime = LocalDateTime.now()
) {
}