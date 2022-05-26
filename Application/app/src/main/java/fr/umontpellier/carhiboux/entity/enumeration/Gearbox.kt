package fr.umontpellier.carhiboux.entity.enumeration

import android.view.View
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.Translatable

enum class Gearbox(private val stringId : Int) : Translatable, Jsonable
{
    ANY(R.string.any),
    AUTOMATIC(R.string.automatic),
    MANUAL(R.string.manual),
    SEQUENTIAL(R.string.sequential);

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
        fun forView(view: Int) : Gearbox
        {
            return when(view) {
                AUTOMATIC.view -> AUTOMATIC
                MANUAL.view -> MANUAL
                SEQUENTIAL.view -> SEQUENTIAL
                else -> ANY
            }
        }

        fun plainValues() : Array<Gearbox>
        {
            val values : MutableList<Gearbox> = mutableListOf()

            for(value in values())
            {
                if(value != ANY)
                {
                    values.add(value)
                }
            }

            return values.toTypedArray()
        }

        fun fromJSON(json: String) : Gearbox
        {
            return when(json)
            {
                AUTOMATIC.name -> AUTOMATIC
                MANUAL.name -> MANUAL
                SEQUENTIAL.name -> SEQUENTIAL
                else -> ANY
            }
        }
    }
}