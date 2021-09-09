package maciej.s.compass

import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import maciej.s.compass.location.LocationOperation

class MainRepository {

    private val locationSettings = LocationSettings()
    private val permissionManager = PermissionManager()
    private val buildVersionChecker = BuildVersionChecker()
    private lateinit var locationOperation: LocationOperation


    fun locationSettingsResponseTaskForFastRequesting(activity: Activity): Task<LocationSettingsResponse> {
        return locationSettings.locationFastRequestBuilder(activity)
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return permissionManager.checkSelfPermission(context,permission)
    }

    fun isBuildVersionMoreThan23(): Boolean {
        return buildVersionChecker.isBuildVersionMoreThan23()
    }

    fun setDestination(location: Location) {
        locationOperation = LocationOperation(location)
    }

    fun calculateDistance(): Float {
        return locationOperation.calculateDistance()
    }

    fun setCurrentPosition(location: Location) {
        locationOperation.setCurrentLocation(location)
    }

    fun calculateBearing(): Float {
        return locationOperation.calculateBearing()
    }


}
