package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class RegistroViewModel: ViewModel() {

    var correo by mutableStateOf("")
        private set
    var pass by mutableStateOf("")
        private set

    var confirmarPass by mutableStateOf("")
        private set

    var mostrarErrorPass by mutableStateOf(false)
        private set

    val passCoinciden : Boolean
        get() = pass == confirmarPass && confirmarPass.isNotEmpty()

    val reglas: List<Pair<String, Boolean>>
        get() = listOf(
            "Al menos 8 caracteres" to (pass.length >= 8),
            "Al menos una mayúscula" to pass.any { it.isUpperCase() },
            "Al menos un número" to pass.any { it.isDigit() },
        )

    val formularioPaso1Valido: Boolean
        get() = reglas.all { it.second } && correoValido && passCoinciden

    val correoValido: Boolean
        get() = correo.isNotBlank()

    var nombre by mutableStateOf("")
        private set

    var apellidoPaterno by mutableStateOf("")
        private set

    var apellidoMaterno by mutableStateOf("")  // opcional
        private set

    var ocupacion by mutableStateOf("")
        private set

    var genero by mutableStateOf("")
        private set

    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set

    val generosOpciones = listOf("Masculino", "Femenino", "Prefiero no decir")

    val formularioPaso2Valido: Boolean
        get() = nombre.isNotBlank() &&
                apellidoPaterno.isNotBlank() &&
                ocupacion.isNotBlank() &&
                genero.isNotBlank() &&
                fechaNacimiento != null
    fun onCorreoChange(value: String) {
        correo = value
    }

    fun onPassChange(value: String) {
        pass = value
    }

    fun onConfirmarPassChange(value: String) {
        confirmarPass = value
        mostrarErrorPass = false
    }

    fun onConfirmarPassFocusLost() {
        if (confirmarPass.isNotEmpty()) {
            mostrarErrorPass = !passCoinciden
        }
    }

    fun onSiguienteClickPaso1(): Boolean {
        mostrarErrorPass = !passCoinciden
        return formularioPaso1Valido
    }

    fun onNombreChange(value: String) {
        nombre = value
    }

    fun onApellidoPaternoChange(value: String) {
        apellidoPaterno = value

    }
    fun onApellidoMaternoChange(value: String) {
        apellidoMaterno = value
    }

    fun onOcupacionChange(value: String) {
        ocupacion = value
    }

    fun onGeneroChange(value: String) {
        genero = value
    }

    fun onFechaNacimientoChange(value: LocalDate) {
        fechaNacimiento = value
    }

    fun onSiguienteClickPaso2(): Boolean {
        return formularioPaso2Valido
    }
}