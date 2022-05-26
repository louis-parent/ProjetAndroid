package fr.umontpellier.carhiboux.repository.implementation.mock

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.entity.notification.Notification
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object MockNotificationRepository : MockRepository<Notification>(arrayOf(
    Notification(LocalDateTime.now(), NotificationType.ALERT, 0, 1, false, 0),
    Notification(LocalDateTime.now(), NotificationType.ALERT, 1, 1, true, 1)
))
{
    override fun readAll(callback: (List<Notification>) -> Unit)
    {
        super.readAll { notifications ->
            for(notification in notifications)
            {
                if(!notification.notified)
                {
                    update(notification.requireId(), notification.hasBeenNotified()) {}
                }
            }

            callback.invoke(notifications)
        }
    }
    override fun readIf(test: (Notification) -> Boolean, callback: (List<Notification>) -> Unit)
    {
        super.readIf(test) { notifications ->
            for(notification in notifications)
            {
                if(!notification.notified)
                {
                    update(notification.requireId(), notification.hasBeenNotified()) {}
                }
            }

            callback.invoke(notifications)
        }
    }

    override fun read(id: Long, callback: (Notification?) -> Unit)
    {
        super.read(id) { notification ->
            if(notification != null && !notification.notified)
            {
                update(notification.requireId(), notification.hasBeenNotified()) {}
            }

            callback.invoke(notification)
        }
    }
}