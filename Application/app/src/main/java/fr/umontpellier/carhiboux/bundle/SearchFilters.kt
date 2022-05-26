package fr.umontpellier.carhiboux.bundle

import android.os.Parcelable
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.enumeration.AnnouncementType
import fr.umontpellier.carhiboux.entity.enumeration.Energy
import fr.umontpellier.carhiboux.entity.enumeration.Gearbox
import fr.umontpellier.carhiboux.utils.Jsonable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.util.*

@Parcelize
@Serializable
class SearchFilters(
    val search: String? = null,
    val type: AnnouncementType = AnnouncementType.ANY,
    val brand: String? = null,
    val model: String? = null,
    val maxPrice: Int = PRICE_MAX_VALUE,
    val minPrice: Int = 0,
    val maxKilometers: Int = KILOMETERS_MAX_VALUE,
    val minYear: Int = 1971,
    val maxYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val technicalControlRequired: Boolean = false,
    val energy: Energy = Energy.ANY,
    val gearbox: Gearbox = Gearbox.ANY,
    val minPlaces: Int = 1,
    val announcer : Long? = null
) : Parcelable, Jsonable
{
    fun isAdvanced() : Boolean
    {
        return !(type == AnnouncementType.ANY
                && (brand == null || brand.isEmpty())
                && (model == null || model.isEmpty())
                && maxPrice == PRICE_MAX_VALUE
                && minPrice == 0
                && maxKilometers == KILOMETERS_MAX_VALUE
                && minYear == 1971
                && maxYear == Calendar.getInstance().get(Calendar.YEAR)
                && !technicalControlRequired
                && energy == Energy.ANY
                && gearbox == Gearbox.ANY
                && minPlaces == 1)
    }

    override fun toString(): String
    {
        var str = ""

        if(search != null && search != "")
        {
            str = search
        }
        else
        {
            if(type != AnnouncementType.ANY)
            {
                str += "| $type"
            }

            if(brand != null)
            {
                str += "| $brand"
            }

            if(model !== null)
            {
                str += "| $model"
            }

            if(minYear != 1971 || maxYear != Calendar.getInstance().get(Calendar.YEAR))
            {
                str += "| $minYear - $maxYear"
            }

            if(technicalControlRequired)
            {
                str += "| technical control"
            }

            if(energy != Energy.ANY)
            {
                str += "| $energy"
            }

            if(gearbox != Gearbox.ANY)
            {
                str += "| $gearbox"
            }

            if(minPlaces > 1)
            {
                str += "| places > $minPlaces"
            }

            str += " |"
        }

        return str
    }

    fun match(announcement: Announcement) : Boolean
    {
        var match = true
        val lowerSearch = search?.lowercase() ?: ""

        match = match && (
            search == null ||
            announcement.brand.lowercase().contains(lowerSearch) ||
            announcement.model.lowercase().contains(lowerSearch) ||
            announcement.type.toString().lowercase().contains(lowerSearch) ||
            announcement.year.toString().contains(lowerSearch) ||
            announcement.gearbox.toString().lowercase().contains(lowerSearch) ||
            announcement.energy.toString().lowercase().contains(lowerSearch)
        )
        match = match && (type == AnnouncementType.ANY || announcement.type == type)
        match = match && (brand == null || announcement.brand.lowercase().contains(brand.lowercase()))
        match = match && (model == null || announcement.model.lowercase().contains(model.lowercase()))
        match = match && announcement.price <= maxPrice
        match = match && announcement.price >= minPrice
        match = match && announcement.kilometers <= maxKilometers
        match = match && announcement.year <= maxYear
        match = match && announcement.year >= minYear
        match = match && (!technicalControlRequired || announcement.technicalControl)
        match = match && (energy == Energy.ANY || announcement.energy == energy)
        match = match && (gearbox == Gearbox.ANY || announcement.gearbox == gearbox)
        match = match && announcement.places >= minPlaces
        match = match && (announcer == null || announcement.author == announcer)

        return match
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("search", search)
        json.put("type", type)
        json.put("brand", brand)
        json.put("model", model)
        json.put("maxPrice", maxPrice)
        json.put("minPrice", minPrice)
        json.put("maxKilometers", maxKilometers)
        json.put("minYear", minYear)
        json.put("maxYear", maxYear)
        json.put("technicalControlRequired", technicalControlRequired)
        json.put("energy", energy)
        json.put("gearbox", gearbox)
        json.put("minPlaces", minPlaces)
        json.put("announcer", announcer)

        return json.toString()
    }

    companion object
    {
        const val KILOMETERS_MAX_VALUE : Int = 500000
        const val PRICE_MAX_VALUE : Int = 9999999

        fun searchWithFilters(search: String?, filters: SearchFilters) : SearchFilters
        {
            return SearchFilters(
                search,
                filters.type,
                filters.brand,
                filters.model,
                filters.maxPrice,
                filters.minPrice,
                filters.maxKilometers,
                filters.minYear,
                filters.maxYear,
                filters.technicalControlRequired,
                filters.energy,
                filters.gearbox,
                filters.minPlaces,
                filters.announcer
            )
        }

        fun fromJSON(json: String) : SearchFilters
        {
            val obj = JSONObject(json)

            return SearchFilters(
                nullableJSON(obj, "search"),
                AnnouncementType.fromJSON(obj.get("type").toString()),
                nullableJSON(obj, "brand"),
                nullableJSON(obj, "model"),
                obj.getInt("maxPrice"),
                obj.getInt("minPrice"),
                obj.getInt("maxKilometers"),
                obj.getInt("minYear"),
                obj.getInt("maxYear"),
                obj.getBoolean("technicalControlRequired"),
                Energy.fromJSON(obj.get("energy").toString()),
                Gearbox.fromJSON(obj.get("gearbox").toString()),
                obj.getInt("minPlaces"),
                nullableJSON(obj, "announcer"),
            )
        }

        private fun <T> nullableJSON(json: JSONObject, key: String) : T?
        {
            return if(json.optString(key) == "null") {
                null
            } else {
                json.opt(key) as T
            }
        }
    }
}