package eg.gov.iti.jets.project.alerts.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.app.NotificationCompat
import eg.gov.iti.jets.project.MainActivity
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.database.ConcreteLocalSource
import eg.gov.iti.jets.project.model.Repository
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        var name = ""
        var description = ""
        val geocoder = Geocoder(context)
        val lat = intent.extras?.getString("lat")
        val lon = intent.extras?.getString("lon")
        val id = intent.extras?.getString("id")!!.toInt()
        val address:String = try {
            val list = geocoder.getFromLocation(lat!!.toDouble(), lon!!.toDouble(), 3) as MutableList<Address>
            list[0].adminArea.toString() + ", " + list[0].countryName.toString()
        }catch (e:java.lang.Exception){
            "$lat , $lon"
        }
        runBlocking(Dispatchers.IO) {
            Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(context)).getLocationWeather(lat!!,lon!!,"en").collect{
                if (it.alerts==null) {
                    name = "All Safe"
                    description = "There is no weather alerts in $address"
                }else{
                    name = it.alerts[0].event + " in $address"
                    description = it.alerts[0].description
                }
            }
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel("channel1", name, NotificationManager.IMPORTANCE_HIGH)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }
        val actionActivityIntent = Intent(context,MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(context,0,actionActivityIntent,PendingIntent.FLAG_IMMUTABLE)

        val actionBroadcastIntent = Intent(context,NotificationReceiver::class.java)
        val broadcastPendingIntent = PendingIntent.getBroadcast(context,id,actionBroadcastIntent,PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context,"channel1")
            .setSmallIcon(R.drawable.settings)
            .setContentTitle(address)
            .setContentText(name)
            .addAction(R.drawable.settings,"Dismiss",broadcastPendingIntent)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(description))
            .setPriority(NotificationCompat.PRIORITY_MAX)
        notificationManager.notify(1,builder.build())

        val pref:SharedPreferences = context.getSharedPreferences(Setup.SettingsSharedPref,Context.MODE_PRIVATE)
        if(pref.getString(Setup.SettingsSharedPrefAlerts,"notifications")=="alarms") {
            val intentService = Intent(context, AlarmService::class.java)
            context.startService(intentService)
        }
    }
}