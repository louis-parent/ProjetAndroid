package fr.umontpellier.carhiboux.fragment.notification.list

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.NotificationListAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class NotificationListFragment: Fragment()
{
    private var userId : Long? = null
    private var showMessages : Boolean  = true
    private var showAlerts : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        showMessages = (arguments?.get(BundleKeys.SHOW_MESSAGES) ?: true) as Boolean
        showAlerts = (arguments?.get(BundleKeys.SHOW_ALERTS) ?: true) as Boolean
        userId = arguments?.get(BundleKeys.USER) as Long?

        when
        {
            userId == null && (activity as MainActivity).isConnectedUser() -> {
                userId = (activity as MainActivity).getConnectedUser()!!.id
            }
            userId == null -> {
                findNavController().navigate(R.id.to_announcement_list)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.notification_list_fragment, container, false)

        view.findViewById<SwitchMaterial>(R.id.notification_list_show_messages).isChecked = showMessages
        view.findViewById<SwitchMaterial>(R.id.notification_list_show_alerts).isChecked = showAlerts

        val adapter = NotificationListAdapter(requireActivity(), userId!!, showMessages, showAlerts)

        view.findViewById<ListView>(R.id.notification_list).adapter = adapter

        view.findViewById<ImageButton>(R.id.notification_list_mark_all_read).setOnClickListener {
            adapter.markAllSeen()
        }

        view.findViewById<SwitchMaterial>(R.id.notification_list_show_messages).setOnCheckedChangeListener { _, checked ->
            findNavController().navigate(R.id.to_notification_list, bundleOf(
                BundleKeys.SHOW_MESSAGES to checked,
                BundleKeys.SHOW_ALERTS to showAlerts,
                BundleKeys.USER to userId
            ))
        }

        view.findViewById<SwitchMaterial>(R.id.notification_list_show_alerts).setOnCheckedChangeListener { _, checked ->
            findNavController().navigate(R.id.to_notification_list, bundleOf(
                BundleKeys.SHOW_MESSAGES to showMessages,
                BundleKeys.SHOW_ALERTS to checked,
                BundleKeys.USER to userId
            ))
        }

        view.findViewById<ListView>(R.id.notification_list).setOnItemClickListener { _: AdapterView<*>, _: View, index: Int, _: Long ->
            val notification = adapter.getItem(index)
            notification.markSeen().save {}

            if(notification.type == NotificationType.ALERT)
            {
                findNavController().navigate(R.id.from_notification_list_to_announcement_details, bundleOf(
                    BundleKeys.ANNOUNCEMENT to notification.data
                ))
            }
            else if(notification.type == NotificationType.MESSAGE)
            {
                RepositoryFactory.getPreferredFactory().getConversationRepository().read(notification.data) { conversation ->
                    RepositoryFactory.getPreferredFactory().getAnnouncementRepository().read(conversation!!.announcement) { announcement ->
                        requireActivity().runOnUiThread {
                            findNavController().navigate(R.id.from_notification_list_to_chat, bundleOf(
                                BundleKeys.ANNOUNCEMENT to announcement,
                                BundleKeys.DESTINATION to if(conversation.user1 == userId) conversation.user2 else conversation.user1
                            ))
                        }
                    }
                }
            }
        }

        return view
    }
}