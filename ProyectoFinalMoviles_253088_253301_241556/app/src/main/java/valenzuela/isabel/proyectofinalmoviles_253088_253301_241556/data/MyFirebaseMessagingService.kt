package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FIREBASE_MSG", "Mensaje de: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("FIREBASE_MSG", "Cuerpo del mensaje: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FIREBASE_MSG", "Nuevo Token generado: $token")
    }
}