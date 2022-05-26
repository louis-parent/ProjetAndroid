package fr.umontpellier.carhiboux.entity.notification

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.json.JSONLocalDateTime
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable
import java.time.LocalDateTime

data class Notification (
    val dateTime: LocalDateTime,
    val type: NotificationType,
    val data: Long,
    val user: Long,
    var seen: Boolean = false,
    var id: Long? = null,
    val notified: Boolean = false
) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun identifiedClone(id: Long): Notification
    {
        return Notification(
            dateTime,
            type,
            data,
            user,
            seen,
            id,
            notified
        )
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        if(id != null)
        {
            RepositoryFactory.getPreferredFactory().getNotificationRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getNotificationRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getNotificationRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    fun markSeen(): Notification
    {
        seen = true

        return this
    }

    fun hasBeenNotified() : Notification
    {
        return Notification(
            dateTime,
            type,
            data,
            user,
            seen,
            id,
            true
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("dateTime", JSONLocalDateTime.toJSON(dateTime))
        json.put("type", type.toJSON())
        json.put("data", data)
        json.put("user", user)
        json.put("seen", seen)
        json.put("notified", notified)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromJSON(json: String) : Notification
        {
            val obj = JSONObject(json)

            return Notification(
                JSONLocalDateTime.fromJSON(obj.get("dateTime").toString()),
                NotificationType.fromJSON(obj.get("type").toString()),
                obj.getLong("data"),
                obj.getLong("user"),
                obj.getBoolean("seen"),
                obj.getLong("id"),
                obj.getBoolean("notified")
            )
        }
    }
}