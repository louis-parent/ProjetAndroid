package fr.umontpellier.carhiboux.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.entity.notification.Notification
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class NotificationListAdapter(activity: Activity, userId: Long, showMessages : Boolean = true, showAlerts : Boolean = true) : BaseAdapter()
{
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var notifications : List<Notification> = listOf()

    init {
        RepositoryFactory.getPreferredFactory().getNotificationRepository().readIf({
            it.user == userId && ((it.type == NotificationType.ALERT && showAlerts) || (it.type == NotificationType.MESSAGE && showMessages))
        }) {
            notifications = it

            activity.runOnUiThread {
                notifyDataSetChanged()
            }
        }
    }

    override fun getCount(): Int
    {
        return notifications.size
    }

    override fun getItem(index: Int): Notification
    {
        return notifications[index]
    }

    override fun getItemId(index: Int): Long
    {
        return notifications[index].requireId()
    }

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        val v : View = view ?: LayoutInflater.from(group?.context).inflate(R.layout.notification_list_item, group, false)
        val notification = getItem(index)

        v.findViewById<TextView>(R.id.notification_item_title).setText(notification.type.stringId())
        v.findViewById<TextView>(R.id.notification_item_subtitle).text = notification.dateTime.format(formatter)

        val icon = v.findViewById<ImageView>(R.id.notification_item_icon)

        if(notification.seen)
        {
            icon.setColorFilter(ContextCompat.getColor(v.context, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            icon.setImageResource(notification.type.seenIcon)
        }
        else
        {
            icon.setColorFilter(ContextCompat.getColor(v.context, R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN)
            icon.setImageResource(notification.type.unseenIcon)
        }

        return v
    }

    fun markAllSeen()
    {
        for(notification in notifications)
        {
            notification.markSeen().save {}
        }

        notifyDataSetChanged()
    }
}