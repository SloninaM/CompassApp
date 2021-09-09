package maciej.s.compass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import maciej.s.compass.LocationUtils.LOCATION_RECEIVE
import maciej.s.compass.LocationUtils.LATITUDE
import maciej.s.compass.LocationUtils.LONGITUDE
import maciej.s.compass.LocationUtils.INCORRECT_VALUE

class LocationReceiver(private val receiver: MyLocationReceiver):BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!=null && intent.action.equals(LOCATION_RECEIVE)){
            val latitude: Double = intent.getDoubleExtra(LATITUDE, INCORRECT_VALUE)
            val longitude: Double = intent.getDoubleExtra(LONGITUDE, INCORRECT_VALUE)

            if(latitude!= INCORRECT_VALUE || longitude!= INCORRECT_VALUE){
                receiver.onLocationReceive(latitude,longitude)
            }
        }
    }

}