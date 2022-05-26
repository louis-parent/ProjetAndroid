package fr.umontpellier.carhiboux.fragment.alert.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.adapter.AlertListAdapter
import fr.umontpellier.carhiboux.bundle.BundleKeys

class AlertListFragment : Fragment()
{
    private var userId : Long? = null
    private var showEnabledOnly : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        showEnabledOnly = (arguments?.get(BundleKeys.SHOW_ENABLED_ONLY) ?: false) as Boolean
        userId = arguments?.get(BundleKeys.USER) as Long?

        if(userId == null)
        {
            if((activity as MainActivity).isConnectedUser())
            {
                userId = (activity as MainActivity).getConnectedUser()?.id
            }
            else
            {
                findNavController().navigate(R.id.to_login, bundleOf(
                    BundleKeys.REDIRECT to (R.id.to_alert_list to null)
                ))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.alert_list_fragment, container, false)

        val enableOnly = view.findViewById<CheckBox>(R.id.alert_list_enable_only)
        enableOnly.isChecked = showEnabledOnly
        enableOnly.setOnCheckedChangeListener { _, isChecked ->
            findNavController().navigate(R.id.to_alert_list, bundleOf(
                BundleKeys.USER to userId,
                BundleKeys.SHOW_ENABLED_ONLY to isChecked
            ))
        }

        view.findViewById<ListView>(R.id.alert_list).adapter = AlertListAdapter(requireActivity(), userId!!, showEnabledOnly)

        view.findViewById<FloatingActionButton>(R.id.alert_list_create).setOnClickListener {
            findNavController().navigate(R.id.from_alert_list_to_alert_editor, bundleOf(
                BundleKeys.USER to userId
            ))
        }

        return view
    }
}