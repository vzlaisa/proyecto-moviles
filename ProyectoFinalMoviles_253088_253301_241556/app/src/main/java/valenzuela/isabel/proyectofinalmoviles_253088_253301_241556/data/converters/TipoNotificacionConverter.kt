package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters

import androidx.room.TypeConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.TipoNotificacion

class TipoNotificacionConverter {

    @TypeConverter
    fun fromTipoNotificacion(value: TipoNotificacion): String {
        return value.name
    }

    @TypeConverter
    fun toTipoNotificacion(value: String): TipoNotificacion {
        return try {
            TipoNotificacion.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // Valor por defecto en caso de que llegue un tipo extraño
            TipoNotificacion.MENSAJE_SISTEMA
        }
    }

}