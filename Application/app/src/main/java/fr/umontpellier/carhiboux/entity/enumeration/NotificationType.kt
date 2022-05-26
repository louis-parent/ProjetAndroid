package fr.umontpellier.carhiboux.entity.enumeration

import androidx.core.app.NotificationCompat
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.Translatable

enum class NotificationType(private val string: Int, val seenIcon: Int, val unseenIcon: Int, val systemCategory: String) : Translatable, Jsonable
{
    ALERT(R.string.announcement_matching_alert, R.drawable.ic_baseline_notifications_none_24, R.drawable.ic_baseline_notifications_active_24, NotificationCompat.CATEGORY_EVENT),
    MESSAGE(R.string.new_message_received, R.drawable.ic_baseline_chat_bubble_outline_24, R.drawable.ic_baseline_mark_unread_chat_alt_24, NotificationCompat.CATEGORY_MESSAGE);

    override fun stringId(): Int
    {
        return string
    }

    override fun toJSON(): String
    {
        return name
    }

    companion object
    {
        fun fromJSON(json: String): NotificationType
        {
            return when(json)
            {
                ALERT.name -> ALERT
                MESSAGE.name -> MESSAGE
                else -> throw IllegalArgumentException("Notification type $json doesn't exist")
            }
        }
    }
}