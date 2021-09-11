package maciej.s.compass.main

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import maciej.s.compass.helper.BuildVersionChecker
import maciej.s.compass.sensors.CompassSensorsManager
import maciej.s.compass.location.LocationSettings
import maciej.s.compass.helper.PermissionManager
import maciej.s.compass.location.LocationOperation

class MainRepository {

    private val locationSettings = LocationSettings()
    private val permissionManager = PermissionManager()
    private val buildVersionChecker = BuildVersionChecker()
    private lateinit var locationOperation: LocationOperation
    private var compassSensorsManager: CompassSensorsManager? = null


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

    fun setCompassSensors(
        sensorManager: SensorManager,
        sensorMagneticField: Sensor,
        sensorAccelerometer: Sensor
    ){
        compassSensorsManager = CompassSensorsManager(sensorManager,sensorMagneticField,sensorAccelerometer)
    }

    fun pauseCompassSensor() {
        compassSensorsManager?.pauseCompassSensor()
        compassSensorsManager = null
    }

    fun getCompassRotation(): LiveData<Float> {
        return compassSensorsManager!!.rotation
    }


}
