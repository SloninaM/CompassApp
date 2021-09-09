package maciej.s.compass

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

class MainViewModel: ViewModel() {

    private val repo = MainRepository()


    fun checkLocationTurnOn(activity: Activity): Task<LocationSettingsResponse> {
        return repo.locationSettingsResponseTaskForFastRequesting(activity)
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return repo.isPermissionGranted(context,permission)
    }

    fun isBuildVersionMoreThan23(): Boolean {
        return repo.isBuildVersionMoreThan23()
    }
}