package fr.umontpellier.carhiboux.repository.implementation.mock

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.entity.message.Conversation
import fr.umontpellier.carhiboux.entity.message.Message
import fr.umontpellier.carhiboux.entity.notification.Notification
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object MockMessageRepository : MockRepository<Message>(arrayOf(
    Message(0, 1, 0, "Bonjour, je suis intéressé par votre annonce.", LocalDateTime.now(), 0),
    Message(0, 0, 1, "Bonjour, pour quelles dates ?", LocalDateTime.now(), 1)
))
{
    override fun create(entity: Message, callback: (Long?) -> Unit)
    {
        super.create(entity) { messageId ->
            if(messageId != null)
            {
                MockNotificationRepository.create(Notification(
                    LocalDateTime.now(),
                    NotificationType.MESSAGE,
                    Conversation.generateId(entity.announcement, entity.source, entity.destination),
                    entity.destination
                )) {
                    callback(messageId)
                }
            }
            else
            {
                callback(messageId)
            }
        }
    }
}
