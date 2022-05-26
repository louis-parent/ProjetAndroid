package fr.umontpellier.carhiboux.adapter

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.entity.message.Message
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ChatAdapter(activity: Activity, private val source: Long, private val destination: Long) : BaseAdapter()
{
    private val todayFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val otherDayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var onDataSetChanged: ((ChatAdapter) -> Unit)? = null

    private var messages : List<Message> = listOf()

    init {
        RepositoryFactory.getPreferredFactory().getMessageRepository().readIf({
            (it.source == source && it.destination == destination) || (it.source == destination && it.destination == source)
        }) { list ->
            messages = list.sortedBy {
                it.dateTime
            }

            activity.runOnUiThread {
                notifyDataSetChanged()
            }
        }
    }

    override fun notifyDataSetChanged()
    {
        super.notifyDataSetChanged()
        onDataSetChanged?.invoke(this)
    }

    override fun getCount(): Int
    {
        return messages.size
    }

    override fun getItem(index: Int): Message
    {
        return messages[index]
    }

    override fun getItemId(index: Int): Long
    {
        return messages[index].requireId()
    }

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        val message = getItem(index)

        val v : View = if(message.source == source) {
            LayoutInflater.from(group?.context).inflate(R.layout.message_sended_item, group, false)
        } else {
            LayoutInflater.from(group?.context).inflate(R.layout.message_received_item, group, false)
        }

        v.findViewById<TextView>(R.id.message_content).text = message.text

        v.findViewById<TextView>(R.id.message_date).text = if(message.dateTime.compareTo(LocalDateTime.now()) == 0) {
            todayFormatter.format(message.dateTime)
        } else {
            otherDayFormatter.format(message.dateTime)
        }

        return v
    }

    fun setOnDataSetChanged(listener: (ChatAdapter) -> Unit)
    {
        onDataSetChanged = listener
    }
}