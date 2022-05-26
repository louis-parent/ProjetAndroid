package fr.umontpellier.carhiboux.fragment.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.bundle.SearchFilters


class SearchFilterFragment : Fragment()
{
    private var defaultFilters: SearchFilters? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        defaultFilters = arguments?.get(BundleKeys.FILTERS) as SearchFilters?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.search_filter_fragment, container, false)

        initListeners(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        if(defaultFilters != null)
        {
            view.findViewById<EditText>(R.id.search_filter_field).setText(defaultFilters!!.search)
            view.findViewById<FragmentContainerView>(R.id.search_filter_container).getFragment<FilterFragment>().setDefaultFilters(defaultFilters!!)
        }
    }

    private fun initListeners(view: View)
    {
        view.findViewById<ImageButton>(R.id.search_filter_button).setOnClickListener {
            findNavController().navigate(R.id.from_filters_to_announcement_list, bundleOf(BundleKeys.FILTERS to getFilters()))
        }

        view.findViewById<ImageButton>(R.id.search_filter_cancel).setOnClickListener {
            findNavController().navigate(R.id.from_filters_to_announcement_list)
        }
    }

    private fun getFilters(): SearchFilters {
        val filters = requireView().findViewById<FragmentContainerView>(R.id.search_filter_container).getFragment<FilterFragment>().getFilters()

        return SearchFilters(
            requireView().findViewById<EditText>(R.id.search_filter_field).text.toString(),
            filters.type,
            filters.brand,
            filters.model,
            filters.maxPrice,
            filters.minPrice,
            filters.maxKilometers,
            filters.minYear,
            filters.maxYear,
            filters.technicalControlRequired,
            filters.energy,
            filters.gearbox,
            filters.minPlaces,
            defaultFilters?.announcer
        )
    }
}