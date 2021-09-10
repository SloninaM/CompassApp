package maciej.s.compass

import android.Manifest
import android.app.Activity
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import maciej.s.compass.location.LocationReceiver
import maciej.s.compass.location.LocationService
import maciej.s.compass.location.LocationUtils
import maciej.s.compass.location.MyLocationReceiver

class MainActivity : AppCompatActivity(), MyLocationReceiver,
    DestinationLocationFragment.DestinationLocationListener {

    companion object{
        private const val REQUEST_CHECK_SETTINGS = 35
    }

    private lateinit var mService: LocationService
    private var mBound: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: MainViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){  isGranted->
            if(isGranted){
                startCompass()
            }else{
                displayShortToast("You need 'allow' location. App can't work correctly")
            }
        }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                checkLocationPermission()
            }else{
                checkLocationTurnOn()
            }
        }
    }

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        setSensors()
    }

    private fun setSensors() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorAccelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorMagneticField: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        when {
            sensorMagneticField == null -> {
                displayShortToast("Compass can't work correctly.\n Your device haven't magnetic field sensor")
            }
            sensorAccelerometer == null -> {
                displayShortToast("Compass can't work correctly.\n Your device haven't accelerometer sensor")
            }
            else -> {
                viewModel.setCompassSensors(sensorManager,sensorMagneticField,sensorAccelerometer) //TODO REMEBER TO null this value when onPause and this method in onResume
                viewModel.setImageRotation()
                viewModel.imageRotation.observe(this,{
                    arrowImage.rotation = it
                    setDirectionTrianglePosition()
                })
            }
        }
    }

    private fun setDirectionTrianglePosition() {
        val directionTriangleRotation = viewModel.getDirectionTriangle()
        if (directionTriangleRotation != null) {
            directionTriangleImage.rotation = directionTriangleRotation
        }
    }

    private fun setObservers() {
        viewModel.distanceMeters.observe(this, {
            tvDistance.text = "$it m"
        })
        viewModel.bearing.observe(this,{
            tvBearing.text = "$it"
        })
        viewModel.yourDirectionBearing.observe(this,{
            directionTriangleImage.rotation = it
        })
    }

    override fun onStart() {
        super.onStart()
        val locationReceiver = LocationReceiver(this)
        registerReceiver(locationReceiver, IntentFilter(LocationUtils.LOCATION_RECEIVE))
        Intent(this, LocationService::class.java).also{ intent->
            bindService(intent,connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseCompassSensor()
    }

    override fun onStop() {
        super.onStop()
        mService.stop()
        unbindService(connection)
        mBound = false
    }

    private fun startCompass(){
        mService.startLocationUpdates()
    }

    override fun onLocationReceive(latitude:Double,longitude:Double) {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latitude
        location.longitude = longitude
        viewModel.setCurrentPosition(location)
        viewModel.calculateDistance()
        viewModel.calculateBearing()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onClickButton(view: android.view.View) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = DestinationLocationFragment()
        dialogFragment.listener = this
        dialogFragment.show(ft,"Dialog")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkLocationTurnOn() {
        val task = viewModel.checkLocationTurnOn(this)

        task.addOnSuccessListener {
            checkLocationPermission()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) { }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkLocationPermission() {

        val accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val isGranted = viewModel.isPermissionGranted(this, accessFineLocation)
        val isBuildVersionMoreThan23 = viewModel.isBuildVersionMoreThan23()

        if (isGranted) {
            startCompass()
        } else if (isBuildVersionMoreThan23 && shouldShowRequestPermissionRationale(
                accessFineLocation
            )
        ) {
            //TODO display dialog
            displayShortToast("You need 'allow' location. App can't work correctly")
        } else {
            requestPermissionLauncher.launch(
                accessFineLocation
            )
        }
    }

    private fun displayShortToast(text:String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onChangeDestination(latitude: Double, longitude: Double) {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latitude
        location.longitude = longitude
        viewModel.setDestination(location)

        checkLocationTurnOn()
    }
}