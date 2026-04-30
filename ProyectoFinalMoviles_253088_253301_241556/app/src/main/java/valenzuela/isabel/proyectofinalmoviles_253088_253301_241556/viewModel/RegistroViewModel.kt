package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.viewModel

import android.net.Uri
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.UsuarioEntity
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Genero
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums.Interes
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository.UsuarioRepository
import java.time.LocalDate

class RegistroViewModel(private val repository: UsuarioRepository): ViewModel() {

    private var correoJob: Job? = null

    private var nicknameJob: Job? = null

    // Variables para los valores
    var correo by mutableStateOf("")
        private set
    var pass by mutableStateOf("")
        private set
    var confirmarPass by mutableStateOf("")
        private set
    var nombre by mutableStateOf("")
        private set
    var apellidoPaterno by mutableStateOf("")
        private set
    var apellidoMaterno by mutableStateOf<String?>(null)  // opcional
        private set
    var ocupacion by mutableStateOf("")
        private set
    var genero by mutableStateOf<Genero?>(null)
        private set
    var fechaNacimiento by mutableStateOf<LocalDate?>(null)
        private set
    var nickname by mutableStateOf("")
        private set
    var fotoUri by mutableStateOf<Uri?>(null)
        private set
    var interesesSeleccionados by mutableStateOf<Set<Interes>>(emptySet())
        private set

    // Mensajes de errores para los campos
    var correoError by mutableStateOf<String?>(null)
        private set
    var passError by mutableStateOf<String?>(null)
        private set
    var confirmarPassError by mutableStateOf<String?>(null)
        private set
    var generoError by mutableStateOf<String?>(null)
    var fechaNacimientoError by mutableStateOf<String?>(null)
    var nombreError by mutableStateOf<String?>(null)
    var apellidoPaternoError by mutableStateOf<String?>(null)
    var ocupacionError by mutableStateOf<String?>(null)
    var nicknameError by mutableStateOf<String?>(null)
        private set
    var interesesError by mutableStateOf<String?>(null)
        private set
    var registroError by mutableStateOf<String?>(null)
        private set

    // Variables para validación
    val formularioPaso1Valido: Boolean
        get() = reglas.all { it.second } && correoValido && passError == null && confirmarPassError == null
    val formularioPaso2Valido: Boolean
        get() = nombre.isNotBlank() &&
                apellidoPaterno.isNotBlank() &&
                ocupacion.isNotBlank() &&
                genero != null &&
                fechaNacimiento != null &&
                generoError == null &&
                fechaNacimientoError == null
    val formularioPaso3Valido: Boolean
        get() = nickname.isNotBlank() && nicknameError == null && nickname.length >= 3

    val formularioPaso4Valido: Boolean
        get() = interesesSeleccionados.isNotEmpty() && interesesError == null

    var registroExitoso by mutableStateOf(false)
        private set
    val correoValido: Boolean
        get() = correo.isNotBlank() && EMAIL_ADDRESS.matcher(correo).matches() && correoError == null

    // Reglas para la contraseña
    val reglas: List<Pair<String, Boolean>>
        get() = listOf(
            "Al menos 8 caracteres" to (pass.length >= 8),
            "Al menos una mayúscula" to pass.any { it.isUpperCase() },
            "Al menos un número" to pass.any { it.isDigit() },
            "Las contraseñas coinciden" to (pass == confirmarPass && confirmarPass.isNotEmpty())
        )

    // Opciones de los generos, se usa el enum
    val generosOpciones = Genero.entries.toList()

    fun onCorreoChange(value: String) {
        correoError = null
        correo = value

        correoJob?.cancel()

        if (value.isBlank()) {
            correoError = null
            return
        }

        correoJob = viewModelScope.launch {
            // Validar regex
            if (!EMAIL_ADDRESS.matcher(value).matches()) {
                correoError = "Formato de correo inválido"
            } else {
                delay(500)

                // Consultar solo si el formato es correcto y dejó de escribir
                val existe = repository.correoYaExiste(value)
                correoError = if (existe) "Este correo ya está registrado" else null
            }
        }
    }

    fun onPassChange(value: String) {
        passError = null
        pass = value
    }

    fun onConfirmarPassChange(value: String) {
        confirmarPassError = null
        confirmarPass = value
    }

    fun onNombreChange(value: String) {
        nombreError = null
        nombre = value
    }

    fun onApellidoPaternoChange(value: String) {
        apellidoPaternoError = null
        apellidoPaterno = value

    }
    fun onApellidoMaternoChange(value: String) {
        apellidoMaterno = value
    }

    fun onOcupacionChange(value: String) {
        ocupacionError = null
        ocupacion = value
    }

    fun onGeneroChange(value: Genero) {
        generoError = null
        genero = value
    }

    fun onFechaNacimientoChange(value: LocalDate) {
        fechaNacimientoError = null
        fechaNacimiento = value
    }

    fun onNicknameChange(value: String) {
        nicknameError = null
        nickname = value

        nicknameJob?.cancel()

        if (value.isBlank()) {
            nicknameError = null
            return
        }

        nicknameJob = viewModelScope.launch {
            delay(500)

            val existe = repository.nicknameYaExiste(value)
            nicknameError = if (existe) "Este nickname ya está registrado" else null
        }
    }

    fun onFotoChange(uri: Uri) {
        fotoUri = uri
    }

    fun onInteresToggle(interes: Interes) {
        interesesSeleccionados = if (interes in interesesSeleccionados) {
            interesesSeleccionados - interes
        } else {
            interesesSeleccionados + interes
        }
    }

    fun onSiguienteClickPaso1(): Boolean {
        validarPaso1()
        return formularioPaso1Valido
    }

    fun onSiguienteClickPaso2(): Boolean {
        validarPaso2()
        return formularioPaso2Valido
    }

    fun onSiguienteClickPaso3(): Boolean {
        validarPaso3()
        return formularioPaso3Valido
    }

    fun onFinishClickPaso4(): Boolean {
        validarPaso4()
        return formularioPaso4Valido
    }

    private fun validarPaso1() {
        correoError = if (correo.isBlank()) "El correo es requerido" else null
        passError = if (pass.isBlank()) "La contraseña es requerida" else null
        confirmarPassError = if (confirmarPass.isBlank()) "Confirma tu contraseña" else null
    }

    private fun validarPaso2() {
        nombreError = if (nombre.isBlank()) "El nombre es requerido" else null
        apellidoPaternoError = if (apellidoPaterno.isBlank()) "El apellido es requerido" else null
        ocupacionError = if (ocupacion.isBlank()) "La ocupación es requerida" else null
        generoError = if (genero == null) "El género es obligatorio" else null

        // Validación de fecha
        val hoy = LocalDate.now()
        fechaNacimientoError = when {
            fechaNacimiento == null -> "La fecha es requerida"
            fechaNacimiento!!.isAfter(hoy) -> "La fecha no puede ser en el futuro"
            else -> null
        }
    }

    private fun validarPaso3() {
        nicknameError = if (nickname.isBlank()) "El usuario es requerido" else null
    }

    private fun validarPaso4() {
        interesesError = if (interesesSeleccionados.isEmpty()) "Selecciona al menos un interés" else null
    }

    fun registrarUsuario() {
        registroError = null

        val usuario = UsuarioEntity(
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            nickname = nickname,
            correo = correo,
            contrasenia = pass,
            genero = genero!!,
            ocupacion = ocupacion,
            fechaNacimiento = fechaNacimiento!!,
            fotoPerfil = fotoUri?.toString()
        )

        viewModelScope.launch {
            try {
                repository.registrar(usuario, interesesSeleccionados.toList())

                registroExitoso = true
                registroError = null
                limpiarCampos()
            } catch (e: Exception) {
                registroExitoso = false
                registroError = "Ocurrió un error al registrar. Intenta de nuevo."
                Log.e("REGISTRO", "Error al registrar: ${e.message}")
            }
        }
    }

    private fun limpiarCampos() {
        correo = ""
        pass = ""
        confirmarPass = ""
        nombre = ""
        apellidoPaterno = ""
        apellidoMaterno = ""
        ocupacion = ""
        genero = null
        fechaNacimiento = null
        nickname = ""
        fotoUri = null
        interesesSeleccionados = emptySet()
        resetErrores()
    }

    private fun resetErrores() {
        correoError = null
        passError = null
        confirmarPassError = null
        nombreError = null
        apellidoPaternoError = null
        ocupacionError = null
        generoError = null
        fechaNacimientoError = null
        nicknameError = null
        interesesError = null
        registroError = null
    }
}