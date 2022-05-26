package fr.umontpellier.carhiboux.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType


object CarhibouxNotificationSystem
{
    private const val ALERT_CHANNEL_ID = "Carhiboux_alert"
    private const val MESSAGE_CHANNEL_ID = "Carhiboux_message"

    private val CHANNEL_PER_TYPE : Map<NotificationType, String> = mapOf(
        NotificationType.ALERT to ALERT_CHANNEL_ID,
        NotificationType.MESSAGE to MESSAGE_CHANNEL_ID
    )

    private var nextNotificationId = 0
    private var nextIntentId = 42

    fun initChannel(context: Context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val alertChannel = NotificationChannel(ALERT_CHANNEL_ID, context.getString(R.string.alert_channel_name), NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = context.getString(R.string.alert_channel_description)
            }

            val messageChannel = NotificationChannel(MESSAGE_CHANNEL_ID, context.getString(R.string.message_channel_name), NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = context.getString(R.string.message_channel_description)
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(messageChannel)
        }

    }

    fun createNotification(type: NotificationType, content: String, context: Context)
    {
        val builder = NotificationCompat.Builder(context, CHANNEL_PER_TYPE[type]!!)
            .setSmallIcon(type.unseenIcon)
            .setContentTitle(context.getText(type.stringId()))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(type.systemCategory)

        with(NotificationManagerCompat.from(context))
        {
            notify(nextNotificationId++, builder.build())
        }
    }
}