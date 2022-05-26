package fr.umontpellier.carhiboux.entity.util

import fr.umontpellier.carhiboux.utils.Jsonable
import org.json.JSONObject
import java.io.Serializable

@kotlinx.serialization.Serializable
class Address(
    val address: String,
    val zip: String,
    val city: String,
    val country: String
) : Serializable, Jsonable
{
    override fun toString(): String
    {
        return "$address, $zip $city, $country"
    }

    fun simplified(): String
    {
        return "$zip $city"
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("address", address)
        json.put("zip", zip)
        json.put("city", city)
        json.put("country", country)

        return json.toString()
    }

    companion object
    {
        fun fromJSON(json: String) : Address
        {
            val obj = JSONObject(json)

            return Address(
                obj.getString("address"),
                obj.getString("zip"),
                obj.getString("city"),
                obj.getString("country")
            )
        }
    }
}