package maciej.s.compass

import android.app.Activity
import android.content.Context
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainRepository {

    private val locationSettings = LocationSettings()
    private val permissionManager = PermissionManager()



    fun locationSettingsResponseTaskForFastRequesting(activity: Activity): Task<LocationSettingsResponse> {
        return locationSettings.locationFastRequestBuilder(activity)
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return permissionManager.checkSelfPermission(context,permission)
    }


}
