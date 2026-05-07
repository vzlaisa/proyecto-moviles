package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.state

sealed class UiEstado {
    object Idle : UiEstado()
    object Cargando : UiEstado()
    data class Error(val mensaje: String) : UiEstado()
}