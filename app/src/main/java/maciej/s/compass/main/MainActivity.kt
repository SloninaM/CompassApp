package maciej.s.compass.main

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import maciej.s.compass.fragments.DestinationLocationFragment
import maciej.s.compass.R
import maciej.s.compass.location.*


class MainActivity : AppCompatActivity(), MyLocationReceiver,
    DestinationLocationFragment.DestinationLocationListener {

    companion object{
        private const val REQUEST_CHECK_SETTINGS = 35
    }

    private lateinit var locationReceiver:BroadcastReceiver

    private var mBound: Boolean = false

    private lateinit var tvDistance: TextView
    private lateinit var cvDistance: CardView

    private lateinit var viewModel: MainViewModel

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


    private fun serviceConnection() = LocationServiceConnection.connection

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        tvDistance = findViewById(R.id.tvDistance)
        cvDistance = findViewById(R.id.cvDistance)
        setObservers()
    }


    @SuppressLint("NewApi")
    private fun setObservers() {
        viewModel.distanceMeters.observe(this, {
            val intMeters = it.toInt()
            tvDistance.text = getString(R.string.distance_from_the_destination,intMeters)
        })
        viewModel.shownLocationRationaleSwitcher.observe(this,{
            launchRequestPermission(ACCESS_FINE_LOCATION)
        })
    }

    override fun onStart() {
        super.onStart()
        locationReceiver = LocationReceiver(this)
        registerReceiver(locationReceiver, IntentFilter(LocationUtils.LOCATION_RECEIVE))
        Intent(applicationContext, LocationService::class.java).also{ intent->
            bindService(intent,serviceConnection(), Context.BIND_AUTO_CREATE)
        }
        if(viewModel.isLocationUpdateStarted){
            setLocationInfoVisibility()
            val intent = Intent(this,LocationService::class.java)
            intent.action = LocationService.START
            startService(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        if(viewModel.isLocationUpdateStarted){
            val intent = Intent(this,LocationService::class.java)
            intent.action = LocationService.STOP
            startService(intent)
        }
        unbindService(serviceConnection())
        unregisterReceiver(locationReceiver)
        mBound = false
    }

    private fun startCompass(){
        viewModel.isLocationUpdateStarted = true
        if(viewModel.isLocationUpdateStarted){
            val intent = Intent(this,LocationService::class.java)
            intent.action = LocationService.START
            startService(intent)
        }
    }

    override fun onLocationReceive(latitude:Double,longitude:Double) {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latitude
        location.longitude = longitude
        Log.i("latitude","$latitude")
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
        alertDialog.setTitle(getString(R.string.app_need_location))
        alertDialog.setMessage(getString(R.string.app_need_location_rationale))
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)
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
        setLocationInfoVisibility()
    }

    private fun setLocationInfoVisibility() {
        cvDistance.visibility = View.VISIBLE
        tvDistance.visibility = View.VISIBLE
    }
}