package fr.umontpellier.carhiboux.fragment.chat.list

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.ConversationListAdapter
import fr.umontpellier.carhiboux.adapter.EnumAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Translatable

enum class ListOrder(private val translation: Int) : Translatable
{
    UNSEEN_FIRST(R.string.UNSEEN_FIRST),
    RECENT_FIRST(R.string.RECENT_FIRST);

    override fun stringId() : Int
    {
        return translation
    }
}

class ChatListFragment : Fragment()
{
    private var userId: Long? = null
    private var order: ListOrder = ListOrder.RECENT_FIRST

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        order = arguments?.get(BundleKeys.ORDER) as ListOrder? ?: ListOrder.RECENT_FIRST
        userId = arguments?.getLong(BundleKeys.USER) ?: (activity as MainActivity).getConnectedUser()?.requireId()

        if(userId == null)
        {
            findNavController().navigate(R.id.to_announcement_list)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.chat_list_fragment, container, false)

        val adapter = EnumAdapter(ListOrder.values())
        val orderSpinner = view.findViewById<Spinner>(R.id.chat_list_sort)
        orderSpinner.adapter = adapter
        orderSpinner.setSelection(adapter.getItemIndex(order)!!)

        val list = view.findViewById<ListView>(R.id.chat_list)
        list.adapter = ConversationListAdapter(requireActivity(), userId!!)
        list.setOnItemClickListener { listAdapter, _, index, _ ->
            val conversation = (listAdapter.adapter as ConversationListAdapter).getItem(index)

            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().read(conversation.announcement) { announcement ->
                activity?.runOnUiThread {
                    findNavController().navigate(R.id.from_chat_list_to_chat, bundleOf(
                        BundleKeys.ANNOUNCEMENT to announcement,
                        BundleKeys.DESTINATION to if(conversation.user1 == userId) conversation.user2 else conversation.user1
                    ))
                }
            }
        }

        return view
    }
}