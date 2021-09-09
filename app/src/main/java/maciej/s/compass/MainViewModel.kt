package maciej.s.compass

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

class MainViewModel: ViewModel() {

    private val repo = MainRepository()

    private val _locationOn = MutableLiveData<Boolean>()
        val locationOn: LiveData<Boolean>
            get() = _locationOn

    fun checkLocationTurnOn(activity: Activity): Task<LocationSettingsResponse> {
        return repo.locationSettingsResponseTaskForFastRequesting(activity)
    }



}