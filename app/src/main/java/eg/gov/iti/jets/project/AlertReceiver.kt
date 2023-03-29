package eg.gov.iti.jets.project

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


class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

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