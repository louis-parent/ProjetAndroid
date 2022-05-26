package fr.umontpellier.carhiboux.entity.announcement

import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.entity.enumeration.Energy
import fr.umontpellier.carhiboux.entity.enumeration.Gearbox
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.entity.util.Address
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable

data class Announcement(
    val type: AnnouncementType,
    val brand: String,
    val model: String,
    val price: Double,
    val address: Address,
    val kilometers: Int,
    val year: Int,
    val technicalControl: Boolean,
    val energy: Energy,
    val gearbox: Gearbox,
    val exteriorColor: String,
    val interiorColor: String,
    val doors: Int,
    val places: Int,
    val formerOwnerCount: Int,
    val power: Int,
    val din: Int,
    val co2: Double,
    val consumption: Double,
    val author: Long,
    var isEnhance: Boolean = false,
    var id: Long? = null
    ) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Announcement

        if (type != other.type) return false
        if (brand != other.brand) return false
        if (model != other.model) return false
        if (price != other.price) return false
        if (address != other.address) return false
        if (kilometers != other.kilometers) return false
        if (year != other.year) return false
        if (technicalControl != other.technicalControl) return false
        if (energy != other.energy) return false
        if (gearbox != other.gearbox) return false
        if (exteriorColor != other.exteriorColor) return false
        if (interiorColor != other.interiorColor) return false
        if (doors != other.doors) return false
        if (places != other.places) return false
        if (formerOwnerCount != other.formerOwnerCount) return false
        if (power != other.power) return false
        if (din != other.din) return false
        if (co2 != other.co2) return false
        if (consumption != other.consumption) return false
        if (author != other.author) return false
        if (isEnhance != other.isEnhance) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + brand.hashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + kilometers
        result = 31 * result + year
        result = 31 * result + technicalControl.hashCode()
        result = 31 * result + energy.hashCode()
        result = 31 * result + gearbox.hashCode()
        result = 31 * result + exteriorColor.hashCode()
        result = 31 * result + interiorColor.hashCode()
        result = 31 * result + doors
        result = 31 * result + places
        result = 31 * result + formerOwnerCount
        result = 31 * result + power
        result = 31 * result + din
        result = 31 * result + co2.hashCode()
        result = 31 * result + consumption.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + isEnhance.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun identifiedClone(id: Long) : Announcement
    {
        return Announcement(
            type,
            brand,
            model,
            price,
            address,
            kilometers,
            year,
            technicalControl,
            energy,
            gearbox,
            exteriorColor,
            interiorColor,
            doors,
            places,
            formerOwnerCount,
            power,
            din,
            co2,
            consumption,
            author,
            isEnhance,
            id
        )
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        } else {
            RepositoryFactory.getPreferredFactory().getAnnouncementRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    fun title(): String
    {
        return "$brand $model"
    }

    fun enhance() : Announcement
    {
        isEnhance = true
        return this
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("type", type.toJSON())
        json.put("brand", brand)
        json.put("model", model)
        json.put("price", price)
        json.put("address", JSONObject(address.toJSON()))
        json.put("kilometers", kilometers)
        json.put("year", year)
        json.put("technicalControl", technicalControl)
        json.put("energy", energy.toJSON())
        json.put("gearbox", gearbox.toJSON())
        json.put("exteriorColor", exteriorColor)
        json.put("interiorColor", interiorColor)
        json.put("doors", doors)
        json.put("places", places)
        json.put("formerOwnerCount", formerOwnerCount)
        json.put("power", power)
        json.put("din", din)
        json.put("co2", co2)
        json.put("consumption", consumption)
        json.put("author", author)
        json.put("isEnhance", isEnhance)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        private const val enhancePrice : Double = 10.0
        private const val enhancePremiumPrice : Double = 5.0

        fun getEnhancePrice(user: User?) : Double
        {
            return if(user?.isProfessional == true && (user as ProfessionalUser).isPremium) {
                enhancePremiumPrice
            } else {
                enhancePrice
            }
        }

        fun fromJSON(json: String) : Announcement
        {
            val obj = JSONObject(json)

            return Announcement(
                AnnouncementType.fromJSON(obj.get("type").toString()),
                obj.getString("brand"),
                obj.getString("model"),
                obj.getDouble("price"),
                Address.fromJSON(obj.get("address").toString()),
                obj.getInt("kilometers"),
                obj.getInt("year"),
                obj.getBoolean("technicalControl"),
                Energy.fromJSON(obj.get("energy").toString()),
                Gearbox.fromJSON(obj.get("gearbox").toString()),
                obj.getString("exteriorColor"),
                obj.getString("interiorColor"),
                obj.getInt("doors"),
                obj.getInt("places"),
                obj.getInt("formerOwnerCount"),
                obj.getInt("power"),
                obj.getInt("din"),
                obj.getDouble("co2"),
                obj.getDouble("consumption"),
                obj.getLong("author"),
                obj.getBoolean("isEnhance"),
                obj.getLong("id")
            )
        }
    }
}