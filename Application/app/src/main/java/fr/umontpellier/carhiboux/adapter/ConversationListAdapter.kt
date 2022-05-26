package fr.umontpellier.carhiboux.adapter

import android.app.Activity
import android.graphics.PorterDuff
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
import fr.umontpellier.carhiboux.entity.message.Conversation
import fr.umontpellier.carhiboux.fragment.chat.list.ListOrder
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

@RequiresApi(Build.VERSION_CODES.O)
class ConversationListAdapter(private val activity: Activity, private val userId: Long, order: ListOrder = ListOrder.RECENT_FIRST) : BaseAdapter()
{
    private var conversations : List<Conversation> = listOf()

    init {
        RepositoryFactory.getPreferredFactory().getConversationRepository().readIf ({
            it.user1 == userId || it.user2 == userId
        }) { list ->
            conversations = list

            if(order == ListOrder.RECENT_FIRST)
            {
                RepositoryFactory.getPreferredFactory().getMessageRepository().readAll { messages ->
                    conversations = conversations.sortedBy { conversation ->
                        messages.filter { message ->
                            (message.source == conversation.user1 || message.source == conversation.user2) && (message.destination == conversation.user1 || message.destination == conversation.user2) && message.announcement == conversation.announcement
                        }.maxOf {
                            it.dateTime
                        }
                    }

                    activity.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }
            else if(order == ListOrder.UNSEEN_FIRST)
            {
                RepositoryFactory.getPreferredFactory().getNotificationRepository().readAll { notifications ->
                    conversations = conversations.sortedBy { conversation ->
                        notifications.none { notification ->
                            !notification.seen && notification.user == userId && notification.data == conversation.requireId() && notification.type == NotificationType.MESSAGE
                        }
                    }

                    activity.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getCount(): Int
    {
        return conversations.size
    }

    override fun getItem(index: Int): Conversation
    {
        return conversations[index]
    }

    override fun getItemId(index: Int): Long
    {
        return conversations[index].requireId()
    }

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        val v : View = view ?: LayoutInflater.from(group?.context).inflate(R.layout.chat_list_item, group, false)

        val conversation = getItem(index)
        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().read(conversation.announcement) { announcement ->
            RepositoryFactory.getPreferredFactory().getUserRepository().read(if(conversation.user1 == userId) conversation.user2 else conversation.user1) { user ->
                activity.runOnUiThread {
                    v.findViewById<TextView>(R.id.chat_list_item_title).text = announcement?.title()
                    v.findViewById<TextView>(R.id.chat_list_item_subtitle).text = user?.fullName()

                    val icon = v.findViewById<ImageView>(R.id.chat_list_item_icon)

                    RepositoryFactory.getPreferredFactory().getNotificationRepository().readIf({
                        it.data == conversation.requireId() && it.type == NotificationType.MESSAGE && it.user == userId && !it.seen
                    }) { notifications ->
                        activity.runOnUiThread {
                            if(notifications.isEmpty())
                            {
                                icon.setColorFilter(ContextCompat.getColor(v.context, R.color.black), PorterDuff.Mode.SRC_IN)
                                icon.setImageResource(R.drawable.ic_baseline_chat_bubble_outline_24)
                            }
                            else
                            {
                                icon.setColorFilter(ContextCompat.getColor(v.context, R.color.primary), PorterDuff.Mode.SRC_IN)
                                icon.setImageResource(R.drawable.ic_baseline_mark_unread_chat_alt_24)
                            }
                        }
                    }
                }
            }
        }


        return v
    }
}