package fr.umontpellier.carhiboux.entity.message

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import fr.umontpellier.carhiboux.json.JSONLocalDateTime
import fr.umontpellier.carhiboux.utils.Identifiable
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable
import java.time.LocalDateTime

data class Message(
    val announcement: Long,
    val source: Long,
    val destination: Long,
    val text: String,
    val dateTime: LocalDateTime,
    var id: Long? = null
) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: -1
    }

    override fun identifiedClone(id: Long): Identifiable<Long>
    {
        return Message(
            announcement,
            source,
            destination,
            text,
            dateTime,
            id
        )
    }

    override fun save(callback: (Boolean) -> Unit)
    {
       if(id != null)
       {
           RepositoryFactory.getPreferredFactory().getMessageRepository().update(requireId(), this) {
                callback.invoke(it)
            }
        }
        else
        {
            RepositoryFactory.getPreferredFactory().getMessageRepository().create(this) {
                id = it
                callback.invoke(it != null)
            }
        }
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        RepositoryFactory.getPreferredFactory().getMessageRepository().delete(requireId()) {
            callback.invoke(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("announcement", announcement)
        json.put("source", source)
        json.put("destination", destination)
        json.put("text", text)
        json.put("dateTime", JSONLocalDateTime.toJSON(dateTime))

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromJSON(json: String) : Message
        {
            val obj = JSONObject(json)

            return Message(
                obj.getLong("announcement"),
                obj.getLong("source"),
                obj.getLong("destination"),
                obj.getString("text"),
                JSONLocalDateTime.fromJSON(obj.get("dateTime").toString())
            )
        }
    }
}