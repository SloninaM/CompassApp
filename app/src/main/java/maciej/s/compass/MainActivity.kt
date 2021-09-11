package maciej.s.compass

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import maciej.s.compass.location.LocationReceiver
import maciej.s.compass.location.LocationService
import maciej.s.compass.location.LocationUtils
import maciej.s.compass.location.MyLocationReceiver
import androidx.appcompat.app.AlertDialog
import org.w3c.dom.Text


class MainActivity : AppCompatActivity(), MyLocationReceiver,
    DestinationLocationFragment.DestinationLocationListener {

    companion object{
        private const val REQUEST_CHECK_SETTINGS = 35
    }

    private lateinit var mService: LocationService
    private var mBound: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: MainViewModel

    lateinit var arrowImage: ImageView
    lateinit var directionTriangleImage: ImageView
    lateinit var tvBearing: TextView
    lateinit var tvDistance: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){  isGranted->
            if(isGranted){
                startCompass()
            }else{
                if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                    createInfoDialog()
                }else{
                    displayLongToast(R.string.allow_location_in_app_settings)
                }
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
        arrowImage = findViewById(R.id.arrowImage)
        directionTriangleImage = findViewById(R.id.directionTriangleImage)
        tvBearing = findViewById(R.id.tvBearing)
        tvDistance = findViewById(R.id.tvDistance)
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
                //TODO set this info on viewModel and don't check it again, maybe hidden the image
                displayLongToast(R.string.app_cant_work_correctly_no_magnetic_field_sensor)
            }
            sensorAccelerometer == null -> {
                displayLongToast(R.string.app_cant_work_correctly_no_accelerometer_field_sensor)
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

    @SuppressLint("NewApi")
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
        viewModel.shownLocationRationaleSwitcher.observe(this,{
            launchRequestPermission(ACCESS_FINE_LOCATION)
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
        val isGranted = viewModel.isPermissionGranted(this, ACCESS_FINE_LOCATION)
        val isBuildVersionMoreThan23 = viewModel.isBuildVersionMoreThan23()

        if (isGranted) {
            startCompass()
        } else if (isBuildVersionMoreThan23) {
            if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
            createInfoDialog()
            }else{
                launchRequestPermission(ACCESS_FINE_LOCATION)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun launchRequestPermission(permission: String) {
        requestPermissionLauncher.launch(
            permission
        )
    }

    private fun createInfoDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("App need your location")
        alertDialog.setMessage("The application needs your location to calculate the distance and direction to the destination :)")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ ->
            viewModel.setShownLocationRationaleSwitcher()
            dialog.dismiss()
        }
        alertDialog.show()
    }


    private fun displayLongToast(stringResource:Int){
        val text = getString(stringResource)
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
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