package eg.gov.iti.jets.project.alerts.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent){

        val intentService = Intent(context, AlarmService::class.java)
        context.stopService(intentService)
        NotificationManagerCompat.from(context).cancel(1)

    }
}