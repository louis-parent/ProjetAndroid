package fr.umontpellier.carhiboux.adapter

import fr.umontpellier.carhiboux.utils.Translatable

class EnumAdapter<T>(private val items : Array<T>) : BaseTextAdapter<T>() where T : Enum<T>, T : Translatable
{
    override fun getCount(): Int
    {
        return items.size
    }

    override fun getItem(index: Int): T
    {
        return items[index]
    }

    override fun getItemId(index: Int): Long
    {
        return items[index].ordinal.toLong()
    }

    fun getItemIndex(item : T) : Int?
    {
        for(i in items.indices)
        {
            if(items[i] == item)
            {
                return i
            }
        }

        return null
    }
}