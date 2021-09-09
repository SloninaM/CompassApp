package maciej.s.compass

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import maciej.s.compass.location.LocationReceiver
import maciej.s.compass.location.LocationService
import maciej.s.compass.location.LocationUtils
import maciej.s.compass.location.MyLocationReceiver

class MainActivity : AppCompatActivity(), MyLocationReceiver {

    private lateinit var mService: LocationService
    private var mBound: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){  isGranted->
            if(isGranted){
               displayShortToast("Granted")
                startCompass()
            }else{
                displayShortToast("Not granted")
            }
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 35){
            if(resultCode == Activity.RESULT_OK){
                displayShortToast("OK")
            }else{
                displayShortToast("Cancel")
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onStart() {
        super.onStart()
        val locationReceiver = LocationReceiver(this)
        registerReceiver(locationReceiver, IntentFilter(LocationUtils.LOCATION_RECEIVE))
        Intent(this, LocationService::class.java).also{ intent->
            bindService(intent,connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    private fun startCompass(){
        mService.startLocationUpdates()
    }

    override fun onLocationReceive(latitude:Double,longitude:Double) {
        Toast.makeText(this,"$latitude \n $longitude",Toast.LENGTH_LONG).show()
    }

    fun onClickButton(view: android.view.View) {
        checkLocationTurnOn()
        //checkLocationPermission()
    }

    private fun checkLocationTurnOn() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            displayShortToast("The client can initialize location requests here.")
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            displayShortToast("Exception")
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    displayShortToast("startResolution")
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        35
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }

        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    displayShortToast("You can use the API that requires the permission.")
                    startCompass()
                }
                //TODO when api 23
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    displayShortToast("explain to the user why your app requires this permission")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
            }
                else -> {
                    displayShortToast("requstPermissioLauncher.launch(..)")
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }

    }

    private fun displayShortToast(text:String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }
}