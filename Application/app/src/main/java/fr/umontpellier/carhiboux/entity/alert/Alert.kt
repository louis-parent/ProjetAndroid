package fr.umontpellier.carhiboux.entity.alert

import fr.umontpellier.carhiboux.bundle.SearchFilters
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Identifiable
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable

class Alert(
    val filters: SearchFilters,
    val user: Long,
    var enabled: Boolean = true,
    var id: Long? = null
) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun identifiedClone(id: Long): Identifiable<Long>
    {
        return Alert(
            filters,
            user,
            enabled,
            id
        )
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getAlertRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getAlertRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getAlertRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("filters", JSONObject(filters.toJSON()))
        json.put("user", user)
        json.put("enabled", enabled)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        fun fromJSON(json: String) : Alert
        {
            val obj = JSONObject(json)

            return Alert(
                SearchFilters.fromJSON(obj.get("filters").toString()),
                obj.getLong("user"),
                obj.getBoolean("enabled"),
                obj.getLong("id")
            )
        }
    }
}