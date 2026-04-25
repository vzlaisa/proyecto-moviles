package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class DateConverter {
    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun toLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun fromLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun toLocalDateTime(value: LocalDateTime?): String? = value?.toString()
}