package fr.umontpellier.carhiboux.fragment.alert.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.activity.MainActivity
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.dialog.ConfirmDialogFragment
import fr.umontpellier.carhiboux.dialog.ConfirmDialogListener
import fr.umontpellier.carhiboux.entity.alert.Alert
import fr.umontpellier.carhiboux.fragment.filter.FilterFragment
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class AlertEditorFragment : Fragment(), ConfirmDialogListener
{
    private var alert : Alert? = null
    private var userId : Long? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        alert = arguments?.get(BundleKeys.ALERT) as Alert?
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
        val view : View = inflater.inflate(R.layout.alert_editor_fragment, container, false)

        val delete = view.findViewById<Button>(R.id.alert_editor_delete)
        delete.isVisible = alert != null
        delete.setOnClickListener {
            val dialog = ConfirmDialogFragment(R.string.confirm_deletion)
            dialog.setConfirmListener(this)
            dialog.show(childFragmentManager, "ConfirmDialogListener")
        }

        view.findViewById<Button>(R.id.alert_editor_validate).setOnClickListener {
            val filters = view.findViewById<FragmentContainerView>(R.id.alert_editor_container).getFragment<FilterFragment>().getFilters()

            if(alert != null)
            {
                RepositoryFactory.getPreferredFactory().getAlertRepository().update(alert!!.requireId(), Alert(
                    filters,
                    userId!!,
                    alert!!.enabled,
                    alert!!.id
                )) {}
            }
            else
            {
                RepositoryFactory.getPreferredFactory().getAlertRepository().create(Alert(
                    filters,
                    userId!!
                )) {}
            }

            findNavController().navigate(R.id.from_alert_editor_to_alert_list)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        if(alert != null)
        {
            view.findViewById<FragmentContainerView>(R.id.alert_editor_container).getFragment<FilterFragment>().setDefaultFilters(alert!!.filters)
        }
    }

    override fun onDialogConfirmationClick()
    {
        RepositoryFactory.getPreferredFactory().getAlertRepository().delete(alert!!.requireId()) { deleted ->
            if(deleted)
            {
                findNavController().navigate(R.id.to_alert_list)
            }
            else
            {
                Toast.makeText(context, R.string.cannot_delete_alert, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDialogCancelClick() {}
}