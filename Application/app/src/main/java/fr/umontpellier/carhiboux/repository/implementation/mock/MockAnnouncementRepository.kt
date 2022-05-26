package fr.umontpellier.carhiboux.repository.implementation.mock

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.entity.enumeration.Energy
import fr.umontpellier.carhiboux.entity.enumeration.Gearbox
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.entity.notification.Notification
import fr.umontpellier.carhiboux.entity.util.Address
import java.time.LocalDateTime

object MockAnnouncementRepository : MockRepository<Announcement>(arrayOf(
    Announcement(AnnouncementType.SELL, "Renault", "Twingo", 12990.0, Address("1, Wall Street", "00000", "New York", "USA"), 5600, 2021, true, Energy.DIESEL, Gearbox.SEQUENTIAL, "Metalic White", "Gray", 5, 4, 1, 4, 65, 117.0, 5.2, 0, true, 0),
    Announcement(AnnouncementType.RENT, "Fiat", "Panda", 75.0, Address("1, Wall Street", "00000", "New York", "USA"), 13465, 2012, true, Energy.GASOLINE, Gearbox.MANUAL, "White", "Black", 5, 5, 3, 12, 8, 64.5, 6.0, 1, false, 1)
))
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun create(entity: Announcement, callback: (Long?) -> Unit)
    {
        super.create(entity) {
            if(it != null)
            {
                MockAlertRepository.readAll { alerts ->
                    for(alert in alerts)
                    {
                        if(alert.filters.match(entity))
                        {
                            MockNotificationRepository.create(Notification(
                                LocalDateTime.now(),
                                NotificationType.ALERT,
                                it,
                                alert.user
                            )){}
                        }
                    }
                }
            }

            callback(it)
        }
    }
}
