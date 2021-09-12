package maciej.s.compass.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CompassSensorsManager(private val sensorManager: SensorManager,private val sensorMagneticField: Sensor,private val sensorAccelerometer: Sensor) {

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)
    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)
    private val sensorDelay = SensorManager.SENSOR_DELAY_NORMAL

    private val magneticFieldEventsListener: SensorEventListener
    private val accelerometerEventsListener: SensorEventListener

    private val _rotation = MutableLiveData(0.0f)
    val rotation: LiveData<Float>
        get() = _rotation

    companion object{
        private const val PI_FLOAT = 3.14159f
        private const val HALF_FULL_ANGLE = 180
    }

    init{
        magneticFieldEventsListener = setMagneticFieldEvents()
        accelerometerEventsListener = setAccelerometerEvents()
        sensorManager.registerListener(magneticFieldEventsListener,sensorMagneticField,sensorDelay)
        sensorManager.registerListener(accelerometerEventsListener,sensorAccelerometer,sensorDelay)
    }

    private fun setMagneticFieldEvents(): SensorEventListener {
        return object: SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if(event!=null) {
                    floatGeoMagnetic = event.values
                    SensorManager.getRotationMatrix(floatRotationMatrix,null,floatGravity,floatGeoMagnetic)
                    SensorManager.getOrientation(floatRotationMatrix, floatOrientation)

                    _rotation.value = - floatOrientation[0] * HALF_FULL_ANGLE / PI_FLOAT
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }
    }

    private fun setAccelerometerEvents(): SensorEventListener {
        return object: SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if(event!= null) {
                    floatGravity = event.values

                    SensorManager.getRotationMatrix(floatRotationMatrix,null,floatGravity,floatGeoMagnetic)
                    SensorManager.getOrientation(floatRotationMatrix, floatOrientation)

                    _rotation.value = - floatOrientation[0] * HALF_FULL_ANGLE / PI_FLOAT
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }
    }

    private fun unregisterListeners(){
        sensorManager.unregisterListener(magneticFieldEventsListener,sensorMagneticField)
        sensorManager.unregisterListener(accelerometerEventsListener,sensorAccelerometer)
    }

    fun pauseCompassSensor() {
        unregisterListeners()
    }
}
