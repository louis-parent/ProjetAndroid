package fr.umontpellier.carhiboux.adapter

import android.R
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import fr.umontpellier.carhiboux.utils.Translatable
import fr.umontpellier.carhiboux.view.AdapterTextView

abstract class BaseTextAdapter<T : Translatable> : BaseAdapter()
{
    abstract override fun getItem(index: Int): T

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        return view ?: AdapterTextView(group?.context?.getString(getItem(index).stringId()), group?.context!!)
    }

}