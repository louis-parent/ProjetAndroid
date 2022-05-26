package fr.umontpellier.carhiboux.entity.user

import fr.umontpellier.carhiboux.entity.util.Address
import org.json.JSONObject
import java.io.Serializable

data class ProfessionalUser(
    override val email: String,
    override val password: String,
    override val address: Address,
    override val phone: String,
    val name: String,
    val siret: String,
    var isPremium: Boolean,
    override var id: Long? = null,
    override val isProfessional: Boolean = true
) : User(), Cloneable, Serializable
{
    override fun clone(): Any
    {
        return ProfessionalUser(
            email,
            password,
            address,
            phone,
            name,
            siret,
            isPremium,
            id
        )
    }

    override fun identifiedClone(id: Long): ProfessionalUser
    {
        return ProfessionalUser(
            email,
            password,
            address,
            phone,
            name,
            siret,
            isPremium,
            id
        )
    }

    override fun fullName(): String
    {
        return name
    }

    fun becomePremium(): ProfessionalUser
    {
        isPremium = true
        return this
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("email", email)
        json.put("password", password)
        json.put("address", JSONObject(address.toJSON()))
        json.put("phone", phone)
        json.put("name", name)
        json.put("siret", siret)
        json.put("isPremium", isPremium)
        json.put("isProfessional", isProfessional)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        private const val premiumPrice: Double = 35.0

        fun getPremiumPrice() : Double
        {
            return premiumPrice
        }

        fun fromJSON(json: String) : ProfessionalUser
        {
            val obj = JSONObject(json)

            return ProfessionalUser(
                obj.getString("email"),
                obj.getString("password"),
                Address.fromJSON(obj.get("address").toString()),
                obj.getString("phone"),
                obj.getString("name"),
                obj.getString("siret"),
                obj.getBoolean("isPremium"),
                obj.getLong("id")
            )
        }
    }
}