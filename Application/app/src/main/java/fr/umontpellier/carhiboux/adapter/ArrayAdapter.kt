package fr.umontpellier.carhiboux.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import fr.umontpellier.carhiboux.view.AdapterTextView

class ArrayAdapter(private val items: Array<String>) : BaseAdapter()
{
    override fun getCount(): Int
    {
        return items.size
    }

    override fun getItem(index: Int): String
    {
        return items[index]
    }

    override fun getItemId(index: Int): Long
    {
        return items[index].hashCode().toLong()
    }

    override fun getView(index: Int, view: View?, group: ViewGroup?): View
    {
        return view ?: AdapterTextView(getItem(index), group?.context!!)
    }
}