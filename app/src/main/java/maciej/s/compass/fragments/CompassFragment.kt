package maciej.s.compass.fragments

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import maciej.s.compass.R
import maciej.s.compass.main.MainViewModel

class CompassFragment: Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var compassImage: ImageView
    private lateinit var directionTriangleImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_compass, container,false)
        compassImage = v.findViewById(R.id.compassImage)
        directionTriangleImage = v.findViewById(R.id.directionTriangleImage)
        return v
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.hasSensors){
        setSensors()
        }else{
            setNoSensors()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel =  ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private fun setSensors() {
        val sensorManager = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorAccelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorMagneticField: Sensor? =
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        when {
            sensorMagneticField == null -> {
                setNoSensors()
                displayLongToast(R.string.app_cant_work_correctly_no_magnetic_field_sensor)
            }
            sensorAccelerometer == null -> {
                setNoSensors()
                displayLongToast(R.string.app_cant_work_correctly_no_accelerometer_field_sensor)
            }
            else -> {
                directionTriangleImage.visibility = View.VISIBLE

                viewModel.setCompassSensors(
                    sensorManager,
                    sensorMagneticField,
                    sensorAccelerometer
                )
                viewModel.setImageRotation()
                viewModel.imageRotation.observe(this, {
                    val duration = viewModel.getDuration(it)
                    compassImage.animate().rotation(it).duration = duration
                    setDirectionTrianglePosition(duration)
                })

            }
        }
    }

    private fun setNoSensors(){
        viewModel.hasSensors = false
        compassImage.alpha = 0.1f
        directionTriangleImage.visibility = View.INVISIBLE
    }

    private fun setDirectionTrianglePosition(duration: Long) {
        val directionTriangleRotation = viewModel.getDirectionTriangle()
        directionTriangleImage.animate().rotation(directionTriangleRotation).duration = duration
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseCompassSensor()
    }

    private fun displayLongToast(stringResource:Int){
        val text = getString(stringResource)
        Toast.makeText(activity,text, Toast.LENGTH_LONG).show()
    }
}