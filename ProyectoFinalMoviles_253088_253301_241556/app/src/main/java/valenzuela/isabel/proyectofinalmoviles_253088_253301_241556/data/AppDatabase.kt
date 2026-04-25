package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.UsuarioDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.DateConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.GeneroConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.converters.InteresConverter
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.InteresEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioInteresCrossRef

@Database(
    entities = [
        UsuarioEntity::class,
        InteresEntity::class,
        UsuarioInteresCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [
    GeneroConverter::class,
    DateConverter::class,
    InteresConverter::class
])
abstract class AppDatabase: RoomDatabase() {

    // daos
    abstract fun usuarioDao(): UsuarioDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "joinly_database"
                ).fallbackToDestructiveMigration(true).build()

                INSTANCE = instance

                instance
            }
        }
    }
}