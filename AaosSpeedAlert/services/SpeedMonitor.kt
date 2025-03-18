import android.location.Location
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SpeedMonitor(private val renterId: String) {
    private val db = Firebase.firestore
    private var currentSpeedLimit: Int = 0

    init {
        fetchSpeedLimit()
    }

    private fun fetchSpeedLimit() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = db.collection("renters").document(renterId).get().await()
                currentSpeedLimit = doc.getLong("speedLimit")?.toInt() ?: 0
                Log.d("SpeedMonitor", "Speed limit for $renterId is $currentSpeedLimit")
            } catch (e: Exception) {
                Log.d("SpeedMonitor", "Error while fetching speed limit", e)
            }
        }
    }

    fun updateSpeed(location: Location, userToken: String) {
        val speedInKmh = location.speed * 3.6
        val latitude = location.latitude
        val longitude = location.longitude

        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (currentSpeedLimit != 0 && speedInKmh > currentSpeedLimit) {
                    val data = mapOf(
                        "renterId" to renterId,
                        "speed" to speedInKmh,
                        "limit" to currentSpeedLimit,
                        "userToken" to userToken,
                        "time" to System.currentTimeMillis(),
                        "latitude" to latitude,
                        "longitude" to longitude
                    )
                    db.collection("speed-exceeded").document(UUID.randomUUID().toString())
                        .set(data).await()
                    Log.d("SpeedMonitor", "Saved speed exceed event")
                }

                // Uncomment to send data to server when available
                /*val speedData = SpeedData(renterId, speedInKmh, latitude, longitude)
                NetworkService.apiService.sendSpeedData(speedData)*/
            } catch (e: Exception) {
                Log.d("SpeedMonitor", "Error saving speed exceed event", e)
            }
        }
    }
}