package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.DateConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.GeneroConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.InteresConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.TipoNotificacionConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.InscripcionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.NotificacionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NotificacionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes

@Database(
    entities = [
        UsuarioEntity::class,
        InteresEntity::class,
        UsuarioInteresCrossRef::class,
        ActividadEntity::class,
        InscripcionEntity::class,
        NotificacionEntity::class,
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(value = [
    GeneroConverter::class,
    DateConverter::class,
    InteresConverter::class,
    TipoNotificacionConverter::class
])
abstract class AppDatabase: RoomDatabase() {

    // daos
    abstract fun usuarioDao(): UsuarioDAO
    abstract fun actividadDao(): ActividadDAO
    abstract fun inscripcionDao(): InscripcionDAO
    abstract fun notificacionDao(): NotificacionDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Para registrar los intereses que existen
        private val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Hilo secundario para no bloquear la creación de la DB
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.usuarioDao()
                        // 10 intereses basados en el enum
                        val interesesBase = Interes.entries.map {
                            InteresEntity(id = it.ordinal + 1, nombre = it)
                        }
                        dao.insertIntereses(interesesBase)
                    }
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "joinly_database"
                )
                    .addCallback(databaseCallback)
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }


}