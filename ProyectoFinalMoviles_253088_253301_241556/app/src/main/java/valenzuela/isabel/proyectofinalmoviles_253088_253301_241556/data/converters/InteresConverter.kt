package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters

import androidx.room.TypeConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import kotlin.collections.joinToString

class InteresConverter {

    @TypeConverter
    fun fromInteres(value: Interes?): String? = value?.name

    @TypeConverter
    fun toInteres(value: String?): Interes? =
        value?.let { Interes.valueOf(it) }
}