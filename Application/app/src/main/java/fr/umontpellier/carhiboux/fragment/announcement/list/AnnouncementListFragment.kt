package fr.umontpellier.carhiboux.fragment.announcement.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.AnnouncementListAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.bundle.SearchFilters

class AnnouncementListFragment : Fragment()
{
    private var filters: SearchFilters? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        filters = arguments?.get(BundleKeys.FILTERS) as SearchFilters?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.announcement_list_fragment, container, false)
        initListeners(view)

        val userId = (activity as MainActivity).getConnectedUser()?.id
        view.findViewById<ListView>(R.id.announcement_list).adapter = AnnouncementListAdapter(requireActivity(), userId, filters)

        if(filters != null)
        {
            view.findViewById<EditText>(R.id.announcement_list_search).setText(filters?.search)
        }

        initAddButton(view)
        initMark(view)

        return view
    }

    private fun initListeners(view: View)
    {
        view.findViewById<ImageButton>(R.id.announcement_list_search_filter).setOnClickListener {
            findNavController().navigate(R.id.from_announcement_list_to_filters, bundleOf(BundleKeys.FILTERS to filters))
        }

        view.findViewById<ImageButton>(R.id.announcement_list_search_button).setOnClickListener {
            val search : String = view.findViewById<EditText>(R.id.announcement_list_search).text.toString()

            val newFilters : SearchFilters = if(filters != null) {
                SearchFilters.searchWithFilters(search, filters!!)
            } else {
                SearchFilters(search)
            }

            findNavController().navigate(R.id.from_announcement_list_to_announcement_list, bundleOf(BundleKeys.FILTERS to newFilters))
        }

        view.findViewById<ListView>(R.id.announcement_list).setOnItemClickListener { adapterView, _, i, _ ->
            findNavController().navigate(R.id.from_announcement_list_to_announcement_details, bundleOf(BundleKeys.ANNOUNCEMENT to adapterView.getItemIdAtPosition(i)))
        }

        view.findViewById<FloatingActionButton>(R.id.announcement_list_add_button).setOnClickListener {
            findNavController().navigate(R.id.from_announcement_list_to_announcement_editor)
        }
    }

    private fun initMark(view: View)
    {
        val mark : View = view.findViewById<ImageView>(R.id.announcement_list_mark)
        if(filters != null && filters!!.isAdvanced())
        {
            mark.visibility = View.VISIBLE
        }
        else
        {
            mark.visibility = View.INVISIBLE
        }
    }

    private fun initAddButton(view: View)
    {
        var visibility : Int = View.INVISIBLE

        if((activity as MainActivity).isConnectedUser())
        {
            visibility = View.VISIBLE
        }

        view.findViewById<FloatingActionButton>(R.id.announcement_list_add_button).visibility = visibility
    }
}
