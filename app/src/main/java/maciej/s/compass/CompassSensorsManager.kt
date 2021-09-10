package maciej.s.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class CompassSensorsManager(private val sensorManager: SensorManager,private val sensorMagneticField: Sensor,private val sensorAccelerometer: Sensor) {

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)
    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)
    private val sensorDelay = SensorManager.SENSOR_DELAY_NORMAL

    private val magneticFieldEventsListener: SensorEventListener
    private val accelerometerEventsListener: SensorEventListener

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

                    Log.i("Compass","Mgn: ${-floatOrientation[0]*100/3.14159}")
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

                    Log.i("Compass","Acc: ${-floatOrientation[0]*100/3.14159}")
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
