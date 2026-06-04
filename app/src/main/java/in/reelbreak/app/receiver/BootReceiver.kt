package `in`.reelbreak.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import `in`.reelbreak.app.service.ScrollTrackerService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, ScrollTrackerService::class.java)
            context.startService(serviceIntent)
        }
    }
}