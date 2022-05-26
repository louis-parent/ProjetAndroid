package fr.umontpellier.carhiboux.entity.enumeration

import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.Translatable

enum class Civility(private val stringId : Int) : Translatable, Jsonable
{
    MISTER(R.string.mister),
    MISS(R.string.miss),
    OTHER(R.string.other);

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
        fun fromJSON(json: String) : Civility
        {
            return when(json)
            {
                MISTER.name -> MISTER
                MISS.name -> MISS
                else -> OTHER
            }
        }
    }
}