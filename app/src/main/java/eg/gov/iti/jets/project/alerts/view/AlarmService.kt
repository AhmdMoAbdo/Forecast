package eg.gov.iti.jets.project.alerts.view

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder

class AlarmService : Service() {

    private lateinit var ringtone: Ringtone

    override fun onCreate() {
        super.onCreate()
        var alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ringtone.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ringtone.isLooping = true
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}