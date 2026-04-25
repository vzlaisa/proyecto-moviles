package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes

@Entity(tableName = "intereses")
data class InteresEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: Interes
) {


}