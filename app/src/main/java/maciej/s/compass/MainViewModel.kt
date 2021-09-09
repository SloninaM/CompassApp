package maciej.s.compass

import android.app.Activity
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

class MainViewModel: ViewModel() {

    private val repo = MainRepository()

    private val _distanceMeters = MutableLiveData<Float>()
    val distanceMeters: LiveData<Float>
        get() = _distanceMeters

    private val _bearing = MutableLiveData<Float>()
    val bearing: LiveData<Float>
        get() = _bearing


    fun checkLocationTurnOn(activity: Activity): Task<LocationSettingsResponse> {
        return repo.locationSettingsResponseTaskForFastRequesting(activity)
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return repo.isPermissionGranted(context,permission)
    }

    fun isBuildVersionMoreThan23(): Boolean {
        return repo.isBuildVersionMoreThan23()
    }

    fun setDestination(location: Location) {
        repo.setDestination(location)
    }

    fun setCurrentPosition(location: Location) {
        repo.setCurrentPosition(location)
    }

    fun calculateDistance(){
        _distanceMeters.value = repo.calculateDistance()
    }

    fun calculateBearing() {
        _bearing.value = repo.calculateBearing()
    }
}