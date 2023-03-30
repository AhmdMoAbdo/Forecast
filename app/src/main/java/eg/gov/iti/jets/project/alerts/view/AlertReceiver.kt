package eg.gov.iti.jets.project.alerts.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Api
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.currentLocation.viewModel.CurrentLocationViewModel
import eg.gov.iti.jets.project.currentLocation.viewModel.CurrentLocationViewModelFactory
import eg.gov.iti.jets.project.database.ConcreteLocalSource
import eg.gov.iti.jets.project.model.Repository
import eg.gov.iti.jets.project.network.ApiClient
import eg.gov.iti.jets.project.network.RemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking


class AlertReceiver : BroadcastReceiver() {

    lateinit var currentLocationViewModel: CurrentLocationViewModel
    lateinit var currentLocationViewModelFactory: CurrentLocationViewModelFactory

    override fun onReceive(context: Context, intent: Intent) {

        var lat = intent.extras?.getString("lat")
        var lon = intent.extras?.getString("lon")
        println("+++++++++++++++++++++++++++$lat, ////  $lon")
        runBlocking {
            Repository.getInstance(ApiClient.getInstance(),ConcreteLocalSource(context)).getLocationWeather(lat!!,lon!!,"en").collect(){
                if (it.alerts==null)println("NONE+++++++++++++++++++++++++++++++++++++++++++++++++++")
            }
        }


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = "Channel"
            val description = "description"
            val channel = NotificationChannel("channel1", name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context,"channel1")
            .setSmallIcon(R.drawable.settings)
            .setContentTitle("Ahmed")
            .setContentText("hahahahhahahahahah")
            .setPriority(NotificationCompat.PRIORITY_MAX)
        notificationManager.notify(1,builder.build())

        val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(4000)
        var alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()
    }

}