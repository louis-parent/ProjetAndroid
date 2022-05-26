package fr.umontpellier.carhiboux.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.entity.alert.Alert
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory

class AlertListAdapter(activity: Activity, userId : Long, showEnabledOnly : Boolean = false) : BaseAdapter()
{
    private var alerts : List<Alert> = listOf()

    init {
        RepositoryFactory.getPreferredFactory().getAlertRepository().readIf({
            it.user == userId && (!showEnabledOnly || it.enabled)
        }) {
            alerts = it

            activity.runOnUiThread {
                notifyDataSetChanged()
            }
        }
    }

    override fun getCount(): Int
    {
        return alerts.size
    }

    override fun getItem(index: Int): Alert
    {
        return alerts[index]
    }

    override fun getItemId(index: Int): Long
    {
        return alerts[index].requireId()
    }

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        val v : View = view ?: LayoutInflater.from(group?.context).inflate(R.layout.alert_list_item, group, false)

        val alert = getItem(index)

        setCorrectIcon(alert, v)
        v.findViewById<TextView>(R.id.alert_item_label).text = alert.filters.toString()

        val enabled = v.findViewById<SwitchMaterial>(R.id.alert_item_toggle)
        enabled.isChecked = alert.enabled
        enabled.setOnCheckedChangeListener { _, isChecked ->
            alert.enabled = isChecked
            RepositoryFactory.getPreferredFactory().getAlertRepository().update(alert.requireId(), alert){ updated ->
                if(updated)
                {
                    setCorrectIcon(alert, v)
                }
            }
        }

        v.findViewById<ImageView>(R.id.alert_item_settings).setOnClickListener {
            v.findNavController().navigate(R.id.from_alert_list_to_alert_editor, bundleOf(
                BundleKeys.ALERT to alert
            ))
        }

        return v
    }

    private fun setCorrectIcon(alert : Alert, view: View)
    {
        val icon = view.findViewById<ImageView>(R.id.alert_item_icon)

        if(alert.enabled)
        {
            icon.setImageResource(R.drawable.ic_baseline_notifications_active_24)
            icon.setColorFilter(R.color.primary)
        }
        else
        {
            icon.setImageResource(R.drawable.ic_baseline_notifications_none_24)
            icon.setColorFilter(R.color.black)
        }
    }
}