package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters

import androidx.room.TypeConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero

class GeneroConverter {

    @TypeConverter
    fun fromGenero(value: Genero?): String? = value?.name

    @TypeConverter
    fun toGenero(value: String?): Genero? = value?.let { Genero.valueOf(it) }
}