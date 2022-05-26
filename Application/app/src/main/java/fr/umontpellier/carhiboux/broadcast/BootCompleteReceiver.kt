package fr.umontpellier.carhiboux.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.umontpellier.carhiboux.notification.NotificationCheckWorker

class BootCompleteReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if(intent?.action.equals("android.intent.action.BOOT_COMPLETED"))
        {
            NotificationCheckWorker.enqueue(context!!)
        }
    }
}