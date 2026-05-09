package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.DateConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.GeneroConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.InteresConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.ActividadDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.InscripcionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.ActividadEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InscripcionEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes

class Converters {
    @TypeConverter
    fun fromInteres(interes: Interes): String = interes.label

    @TypeConverter
    fun toInteres(label: String): Interes {
        return Interes.values().firstOrNull { it.label == label } ?: Interes.DEPORTE
    }
}
@Database(
    entities = [
        UsuarioEntity::class,
        InteresEntity::class,
        UsuarioInteresCrossRef::class,
        ActividadEntity::class,
        InscripcionEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(value = [
    GeneroConverter::class,
    DateConverter::class,
    InteresConverter::class,
    Converters::class
])
abstract class AppDatabase: RoomDatabase() {

    // daos
    abstract fun usuarioDao(): UsuarioDAO
    abstract fun actividadDao(): ActividadDAO
    abstract fun inscripcionDao(): InscripcionDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insertamos los intereses
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (1, 'Deporte')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (2, 'Música')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (3, 'Literatura')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (4, 'Estudios')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (5, 'Videojuegos')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (6, 'Arte')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (7, 'Juegos')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (8, 'Social')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (9, 'Cine')")
                db.execSQL("INSERT INTO intereses (id, nombre) VALUES (10, 'Aire libre')")
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
                    .fallbackToDestructiveMigration(true).build()

                INSTANCE = instance

                instance
            }
        }
    }


}