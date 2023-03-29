package eg.gov.iti.jets.project.currentLocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import eg.gov.iti.jets.project.R

class NotificationHelper(var context: Context): ContextWrapper(context) {

    companion object{
    @Volatile
    private var notificationManger:NotificationManager? = null
        fun getNotManger(context: Context):NotificationManager{
            return notificationManger?: synchronized(this){
                var temp = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManger = temp
                temp
            }
        }
    }

    init {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        var channel  = NotificationChannel("channel","name",NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.enableVibration(true)
        getNotManger(context).createNotificationChannel(channel)

    }

    fun getChannelNotification(name:String,message:String):NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext,"Channel")
            .setContentTitle(name)
            .setContentText(message)
            .setSmallIcon(R.drawable.a01d)
    }
}