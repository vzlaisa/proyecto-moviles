package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.dao.NotificacionDAO
import valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.data.entity.NotificacionEntity

class NotificacionRepository(private val notificacionDAO: NotificacionDAO) {

    private val firestore = FirebaseFirestore.getInstance()

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

        try {
            firestore.collection("notificaciones")
                .document(firestoreId)
                .update(mapOf(
                    "leida" to true
                )).await()
        } catch (e: Exception) {
            Log.w("SYNC", "Sin red al actualizar notificación como leída: ${e.message}")
        }
    }

    suspend fun marcarTodasComoLeidas(userId: Int) {
        notificacionDAO.marcarTodasComoLeidas(userId)

        try {
            // Obtener notificaciones no leídas del usuario de la nube
            val snapshot = firestore.collection("notificaciones")
                .whereEqualTo("idUsuario", userId)
                .whereEqualTo("leida", false)
                .get()
                .await()

            // Crear paquete de operaciones, es para agrupar todas las operaciones en una sola petición
            val batch = firestore.batch()

            for (doc in snapshot.documents) {
                val ref = firestore.collection("notificaciones").document(doc.id)
                batch.update(ref, "leida", true)
            }

            batch.commit().await()

        } catch (e: Exception) {
            Log.w("SYNC", "Error al marcar todas como leídas: ${e.message}")
        }
    }
}