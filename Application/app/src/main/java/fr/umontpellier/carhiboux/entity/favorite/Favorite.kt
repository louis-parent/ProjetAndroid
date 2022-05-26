package fr.umontpellier.carhiboux.entity.favorite

import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Identifiable
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable

data class Favorite(
    val user : Long,
    val announcement: Long,
    var id: Long? = null
) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun identifiedClone(id: Long): Identifiable<Long>
    {
        return Favorite(
            user,
            announcement,
            id
        )
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getFavoriteRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getFavoriteRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getFavoriteRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("user", user)
        json.put("announcement", announcement)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        fun fromJSON(json: String) : Favorite
        {
            val obj = JSONObject(json)

            return Favorite(
                obj.getLong("user"),
                obj.getLong("announcement"),
                obj.getLong("id")
            )
        }
    }
}