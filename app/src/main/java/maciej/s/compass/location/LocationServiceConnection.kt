package maciej.s.compass.location

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

object LocationServiceConnection {

    lateinit var mService: LocationService

    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder  = service as LocationService.LocalBinder
            mService  = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            //check is mBound false
        }

    }

}