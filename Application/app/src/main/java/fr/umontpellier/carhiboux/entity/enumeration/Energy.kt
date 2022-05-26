package fr.umontpellier.carhiboux.entity.enumeration

import android.view.View
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.Translatable

enum class Energy(private val stringId: Int) : Translatable, Jsonable
{
    ANY(R.string.any),
    GASOLINE(R.string.gasoline),
    DIESEL(R.string.diesel),
    ELECTRIC(R.string.electric),
    GAS(R.string.gas);

    val view : Int = View.generateViewId()

    override fun stringId(): Int
    {
        return stringId
    }

    override fun toJSON(): String
    {
        return name
    }

    companion object
    {
        fun forView(view: Int) : Energy
        {
            return when(view) {
                GASOLINE.view -> GASOLINE
                DIESEL.view -> DIESEL
                ELECTRIC.view -> ELECTRIC
                GAS.view -> GAS
                else -> ANY
            }
        }

        fun plainValues() : Array<Energy>
        {
            val values : MutableList<Energy> = mutableListOf()

            for(value in values())
            {
                if(value != ANY)
                {
                    values.add(value)
                }
            }

            return values.toTypedArray()
        }

        fun fromJSON(json: String) : Energy
        {
            return when(json) {
                GASOLINE.name -> GASOLINE
                DIESEL.name -> DIESEL
                ELECTRIC.name -> ELECTRIC
                GAS.name -> GAS
                else -> ANY
            }
        }
    }
}