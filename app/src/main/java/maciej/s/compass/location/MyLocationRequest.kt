package maciej.s.compass.location

import com.google.android.gms.location.LocationRequest

object MyLocationRequest {

    fun getFastLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }
}