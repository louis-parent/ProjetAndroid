package fr.umontpellier.carhiboux.entity.user

import fr.umontpellier.carhiboux.entity.favorite.Favorite
import fr.umontpellier.carhiboux.entity.util.Address
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable

sealed class User : Cloneable, MappedEntity<Long>, Serializable, Jsonable
{
    abstract val email: String
    abstract val password: String
    abstract val address: Address
    abstract val phone: String
    abstract val isProfessional: Boolean
    abstract var id: Long?

    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getUserRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getUserRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getUserRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    abstract fun fullName(): String

    fun isFavorite(announcement: Long, callback: (Boolean) -> Unit)
    {
        getFavorite(announcement) {
            callback.invoke(it != null)
        }
    }

    fun toggleFavorite(announcement: Long, callback: (Boolean) -> Unit)
    {
        getFavorite(announcement) { favorite ->
            if(favorite != null)
            {
                favorite.delete {
                    callback.invoke(false)
                }
            }
            else
            {
                Favorite(requireId(), announcement).save {
                    callback.invoke(true)
                }
            }
        }
    }

    private fun getFavorite(announcement: Long, callback: (Favorite?) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getFavoriteRepository().readIf({
            it.announcement == announcement && it.user == id
        }) { favorites ->
            if(favorites.isEmpty())
            {
                callback.invoke(null)
            }
            else
            {
                callback.invoke(favorites[0])
            }
        }
    }

    companion object
    {
        fun fromJSON(json: String) : User
        {
            val obj = JSONObject(json)

            return if(obj.getBoolean("isProfessional")) {
                ProfessionalUser.fromJSON(json)
            } else {
                ParticularUser.fromJSON(json)
            }
        }
    }
}