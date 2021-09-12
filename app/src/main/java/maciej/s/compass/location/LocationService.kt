package maciej.s.compass.location

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import maciej.s.compass.MyNotificationManager
import maciej.s.compass.R
import maciej.s.compass.location.LocationUtils.LOCATION_RECEIVE
import maciej.s.compass.location.LocationUtils.LATITUDE
import maciej.s.compass.location.LocationUtils.LONGITUDE

class LocationService: Service() {

    companion object{
        const val START = "start"
        const val STOP = "stop"
    }

    private val binder = LocalBinder()

    inner class LocalBinder: Binder(){
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private lateinit var locationServices: FusedLocationProviderClient

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null){
            val action = intent.action
            if(action!= null){
                if(action == START){
                    startLocationUpdates()
                }else if(action == STOP){
                    stop()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "LOCATION_ID"
        val desc = resources.getString(R.string.notification_location_desc)
        val notificationBuilder = MyNotificationManager.getLocationNotification(notificationManager,channelId,desc,applicationContext)

        val locationRequest = MyLocationRequest.getFastLocationRequest()

        locationServices = LocationServices.getFusedLocationProviderClient(this)
        locationServices.requestLocationUpdates(locationRequest,callback, Looper.getMainLooper())
        startForeground(200,notificationBuilder.build())

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

    private fun stop() {
        if(this::locationServices.isInitialized){
            locationServices.removeLocationUpdates(callback)
            stopForeground(true)
            stopSelf()
        }
    }
}