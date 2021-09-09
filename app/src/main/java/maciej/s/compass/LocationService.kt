package maciej.s.compass

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class LocationService: Service() {

    private val binder = LocalBinder()

    inner class LocalBinder: Binder(){
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startLocationUpdates(){
        val exampleIntent = Intent().apply{
            action = "test"
            putExtra("sth1",54.0)
        }
        sendBroadcast(exampleIntent)
    }
}