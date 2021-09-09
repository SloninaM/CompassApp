package maciej.s.compass

import android.app.Activity
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainRepository {

    private val locationSettings = LocationSettings()



    fun locationSettingsResponseTaskForFastRequesting(activity: Activity): Task<LocationSettingsResponse> {
        return locationSettings.locationFastRequestBuilder(activity)
    }


}
