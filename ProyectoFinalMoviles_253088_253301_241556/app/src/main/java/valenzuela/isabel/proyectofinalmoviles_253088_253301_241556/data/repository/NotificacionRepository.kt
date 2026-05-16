package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import kotlinx.coroutines.flow.Flow
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.NotificacionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NotificacionEntity

class NotificacionRepository(private val notificacionDAO: NotificacionDAO) {

    fun getNotificaciones(userId: Int): Flow<List<NotificacionEntity>> {
        return notificacionDAO.getAllNotificaciones(userId)
    }

    fun getNoLeidas(userId: Int): Flow<Int> {
        return notificacionDAO.getContadorNoLeidas(userId)
    }

    suspend fun insertar(notificacion: NotificacionEntity) {
        notificacionDAO.insertar(notificacion)
    }

    suspend fun marcarComoLeida(firestoreId: String) {
        notificacionDAO.marcarComoLeida(firestoreId)
    }

    suspend fun marcarTodasComoLeidas(userId: Int) {
        notificacionDAO.marcarTodasComoLeidas(userId)
    }
}