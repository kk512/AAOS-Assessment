/**
 * Data class representing speed-related data.
 * @param renterId Unique identifier for the renter.
 * @param speed The current speed of the vehicle in km/h.
 * @param latitude The latitude of the vehicle's location.
 * @param longitude The longitude of the vehicle's location.
 */
data class SpeedData(
    val renterId: String,
    val speed: Double,
    val latitude: Double,
    val longitude: Double
)
