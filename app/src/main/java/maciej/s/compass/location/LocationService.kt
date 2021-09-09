package maciej.s.compass.location

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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

    fun startLocationUpdates(){
        val coordinatesIntent = Intent().apply{
            action = LOCATION_RECEIVE
            putExtra(LATITUDE,54.0)
            putExtra(LONGITUDE,25.0)
        }
        sendBroadcast(coordinatesIntent)
    }
}