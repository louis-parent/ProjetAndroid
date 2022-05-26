package fr.umontpellier.carhiboux.entity.user

import fr.umontpellier.carhiboux.entity.enumeration.Civility
import fr.umontpellier.carhiboux.entity.util.Address
import org.json.JSONObject
import java.io.Serializable

data class ParticularUser(
    override val email: String,
    override val password: String,
    override val address: Address,
    override val phone: String,
    val civility: Civility,
    val firstName: String,
    val lastName: String,
    override var id: Long? = null,
    override val isProfessional: Boolean = false
) : User(), Cloneable, Serializable
{
    override fun clone(): Any
    {
        return ParticularUser(
            email,
            password,
            address,
            phone,
            civility,
            firstName,
            lastName,
            id
        )
    }

    override fun identifiedClone(id: Long): ParticularUser
    {
        return ParticularUser(
            email,
            password,
            address,
            phone,
            civility,
            firstName,
            lastName,
            id
        )
    }

    override fun fullName(): String
    {
        return "$firstName $lastName"
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("email", email)
        json.put("password", password)
        json.put("address", JSONObject(address.toJSON()))
        json.put("phone", phone)
        json.put("civility", civility.toJSON())
        json.put("firstName", firstName)
        json.put("lastName", lastName)
        json.put("isProfessional", isProfessional)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        fun fromJSON(json: String) : ParticularUser
        {
            val obj = JSONObject(json)

            return ParticularUser(
                obj.getString("email"),
                obj.getString("password"),
                Address.fromJSON(obj.get("address").toString()),
                obj.getString("phone"),
                Civility.fromJSON(obj.get("civility").toString()),
                obj.getString("firstName"),
                obj.getString("lastName"),
                obj.getLong("id")
            )
        }
    }
}