package fr.umontpellier.carhiboux.entity.enumeration

import android.view.View
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.Translatable

enum class AnnouncementType(private val stringId : Int, val icon: Int, val unit: Int) : Translatable, Jsonable
{
    ANY(R.string.any, R.drawable.ic_baseline_all_inclusive_24, R.string.euro),
    RENT(R.string.rent, R.drawable.ic_baseline_car_rental_24, R.string.euro_per_day),
    SELL(R.string.sell, R.drawable.ic_baseline_sell_24, R.string.euro);

    val view : Int = View.generateViewId()

    override fun stringId() : Int
    {
        return stringId
    }

    override fun toJSON(): String
    {
        return name
    }

    companion object
    {
        fun forView(view: Int) : AnnouncementType
        {
            return when(view) {
                RENT.view -> RENT
                SELL.view -> SELL
                else -> ANY
            }
        }

        fun plainValues() : Array<AnnouncementType>
        {
            val values : MutableList<AnnouncementType> = mutableListOf()

            for(value in values())
            {
                if(value != ANY)
                {
                    values.add(value)
                }
            }

            return values.toTypedArray()
        }

        fun fromJSON(json: String) : AnnouncementType
        {
            return when(json)
            {
                RENT.name -> RENT
                SELL.name -> SELL
                else -> ANY
            }
        }
    }
}