package maciej.s.compass.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import maciej.s.compass.R

object MyNotificationManager {

    fun getLocationNotification(notificationManager: NotificationManager,channelId:String,desc:String,context:Context): NotificationCompat.Builder {

        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_NO_CREATE
        )
        val notificationBuilder = NotificationCompat.Builder(
            context,
            channelId
        )
        notificationBuilder.apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(desc)
            setContentIntent(pendingIntent)
            setAutoCancel(false)
            priority = NotificationCompat.PRIORITY_MAX
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager.getNotificationChannel(channelId) == null){
                val notificationChannel = NotificationChannel(
                    channelId,
                    Service.LOCATION_SERVICE,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.description = desc
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        return notificationBuilder
    }
}