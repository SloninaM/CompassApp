package maciej.s.compass.location

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import maciej.s.compass.location.LocationUtils.LOCATION_RECEIVE
import maciej.s.compass.location.LocationUtils.LATITUDE
import maciej.s.compass.location.LocationUtils.LONGITUDE

class LocationService: Service() {

    private val binder = LocalBinder()

    inner class LocalBinder: Binder(){
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest,callback, Looper.getMainLooper())


    }

    private val callback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
                val latitude = result.lastLocation.latitude
                val longitude = result.lastLocation.longitude
                sendMyBroadcast(latitude,longitude)
            }
        }


    private fun sendMyBroadcast(latitude: Double, longitude: Double) {
        val coordinatesIntent = Intent().apply {
            action = LOCATION_RECEIVE
            putExtra(LATITUDE, latitude)
            putExtra(LONGITUDE, longitude)
        }
        sendBroadcast(coordinatesIntent)
    }
}