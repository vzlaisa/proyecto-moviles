package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.enums

enum class TipoNotificacion(val label: String) {
    NUEVA_SOLICITUD("Solicitud de unión"),       // Alguien pidió unirse (para el creador)
    SOLICITUD_ACEPTADA("Solicitud aprobada"),    // El creador te aceptó (para el interesado)
    SOLICITUD_RECHAZADA("Solicitud rechazada"),  // El creador te rechazó (para el interesado)
    NUEVO_PARTICIPANTE("Nuevo miembro"),         // Alguien se unió directo (en actividad pública)
    MENSAJE_SISTEMA("Aviso")
}