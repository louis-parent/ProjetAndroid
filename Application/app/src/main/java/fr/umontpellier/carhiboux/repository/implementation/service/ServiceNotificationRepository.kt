package fr.umontpellier.carhiboux.repository.implementation.service

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.notification.Notification
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

@RequiresApi(Build.VERSION_CODES.O)
object ServiceNotificationRepository : ServiceRepository<Notification>("notification", Notification::fromJSON)
{
    override fun readIf(test: (Notification) -> Boolean, callback: (List<Notification>) -> Unit)
    {
        super.readIf(test) { notifications ->
            for(notification in notifications)
            {
                if(!notification.notified)
                {
                    RepositoryFactory.getPreferredFactory().getNotificationRepository().update(
                        notification.requireId(),
                        notification.hasBeenNotified()
                    ) {}
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
                RepositoryFactory.getPreferredFactory().getNotificationRepository().update(
                    notification.requireId(),
                    notification.hasBeenNotified()
                ) {}
            }

            callback.invoke(notification)
        }
    }
}