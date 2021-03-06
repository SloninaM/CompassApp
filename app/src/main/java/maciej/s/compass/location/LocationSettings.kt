package maciej.s.compass.location

import android.app.Activity
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class LocationSettings {

    fun locationFastRequestBuilder(activity: Activity,locationRequest: LocationRequest): Task<LocationSettingsResponse> {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        return client.checkLocationSettings(builder.build())
    }


}
