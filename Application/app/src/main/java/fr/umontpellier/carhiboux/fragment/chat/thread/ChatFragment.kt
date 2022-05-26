package fr.umontpellier.carhiboux.fragment.chat.thread

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.ChatAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.entity.message.Conversation
import fr.umontpellier.carhiboux.entity.message.Message
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import java.time.LocalDateTime

class ChatFragment : Fragment()
{
    private var announcement : Announcement? = null
    private var destination : Long? = null
    private var source : Long? = null
    private lateinit var adapter : ChatAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        announcement = arguments?.get(BundleKeys.ANNOUNCEMENT) as Announcement?

        if(announcement == null)
        {
            findNavController().navigate(R.id.to_announcement_list)
        }

        destination = (arguments?.get(BundleKeys.DESTINATION) ?: announcement!!.author) as Long?

        if(!(activity as MainActivity).isConnectedUser())
        {
            findNavController().navigate(R.id.to_login)
        }

        source = (activity as MainActivity).getConnectedUser()!!.requireId()

        val conversation = Conversation(announcement!!.requireId(), source!!, destination!!)

        RepositoryFactory.getPreferredFactory().getNotificationRepository().readIf({
            it.type == NotificationType.MESSAGE && it.user == source && it.data == conversation.requireId() && !it.seen
        }) { notifications ->
            notifications.forEach {
                it.markSeen().save {}
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val view = inflater.inflate(R.layout.chat_fragment, group, false)

        view.findViewById<TextView>(R.id.chat_title).text = announcement!!.brand + " " + announcement!!.model

        view.findViewById<ImageButton>(R.id.chat_send).setOnClickListener {
            val field = view.findViewById<EditText>(R.id.chat_field)
            val text = field.text.toString()

            RepositoryFactory.getPreferredFactory().getMessageRepository().create(Message(announcement!!.requireId(), source!!, destination!!, text, LocalDateTime.now())) {
                requireActivity().runOnUiThread {
                    reloadMessages(view)
                    field.setText("")

                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(field.windowToken, 0)
                }
            }
        }

        reloadMessages(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadMessages(view : View = requireView())
    {
        adapter = ChatAdapter(requireActivity(), source!!, destination!!)
        adapter.setOnDataSetChanged {
            reloadMessageViews(view)
        }

        reloadMessageViews(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadMessageViews(view : View = requireView())
    {
        val container = view.findViewById<LinearLayout>(R.id.chat_messages_container)
        container.removeAllViews()

        for(index in 0 until adapter.count)
        {
            container.addView(adapter.getView(index, null, view as ViewGroup))
        }
    }
}