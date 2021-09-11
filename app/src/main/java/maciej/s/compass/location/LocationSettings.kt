package maciej.s.compass.location

import android.app.Activity
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationSettings {

    fun locationFastRequestBuilder(activity: Activity): Task<LocationSettingsResponse> {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        return client.checkLocationSettings(builder.build())
    }


}
