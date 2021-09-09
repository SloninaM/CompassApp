package maciej.s.compass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationReceiver(private val receiver: MyLocationReceiver):BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!=null && intent.action.equals("test")){
            val sth1: Double = intent.getDoubleExtra("sth1",-300.0)
            if(sth1!= -300.0){
                receiver.onLocationReceive(sth1)
            }
        }
    }

}