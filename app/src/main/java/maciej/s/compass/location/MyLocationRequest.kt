package maciej.s.compass.location

import com.google.android.gms.location.LocationRequest

object MyLocationRequest {

    fun getFastLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            interval = 6000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }
}