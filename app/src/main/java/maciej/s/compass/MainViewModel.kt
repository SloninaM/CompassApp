package maciej.s.compass

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.widget.ImageView
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

    private val _bearing = MutableLiveData<Float>(0f)
    val bearing: LiveData<Float>
        get() = _bearing

    private val _yourDirectionBearing = MutableLiveData<Float>()
    val yourDirectionBearing: LiveData<Float>
        get() = _yourDirectionBearing

    lateinit var imageRotation: LiveData<Float>


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

    fun setCompassSensors(
        sensorManager: SensorManager,
        sensorMagneticField: Sensor,
        sensorAccelerometer: Sensor
    ) {
       repo.setCompassSensors(sensorManager,sensorMagneticField,sensorAccelerometer)
    }

    fun pauseCompassSensor() {
        repo.pauseCompassSensor()
    }

    fun setImageRotation() {
        imageRotation = repo.getCompassRotation()
    }

    fun getDirectionTriangle(): Float? {
        val bearing = bearing.value
        return if(bearing != null){
            bearing + imageRotation.value!!
        }else{
            null
        }

    }
}