package maciej.s.compass

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import maciej.s.compass.location.LocationReceiver
import maciej.s.compass.location.LocationService
import maciej.s.compass.location.LocationUtils
import maciej.s.compass.location.MyLocationReceiver

class MainActivity : AppCompatActivity(), MyLocationReceiver {

    private lateinit var mService: LocationService
    private var mBound: Boolean = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ){  isGranted->
            if(isGranted){
               displayShortToast("Granted")
            }else{
                displayShortToast("Not granted")
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
        checkLocationPermission()
        //startCompass()
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    displayShortToast("You can use the API that requires the permission.")
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