package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun LocalDate.toLongString(): String {
        val locale = Locale.forLanguageTag("es-ES")
        val formatter = DateTimeFormatter.ofPattern("d' de 'MMMM' de 'yyyy", locale)
        return this.format(formatter)
    }

    fun LocalDateTime.toMonthYearString(): String {
        val locale = Locale.forLanguageTag("es-ES")
        val formatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", locale)

        // Primera letra en mayuscula
        return this.format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }
}