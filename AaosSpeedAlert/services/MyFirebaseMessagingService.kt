import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Received a message from ${remoteMessage.from}")
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token generated: $token")
    }
}