package fr.umontpellier.carhiboux.entity.message

import fr.umontpellier.carhiboux.utils.Identifiable
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONObject
import java.io.Serializable

data class Conversation(
    val announcement: Long,
    val user1: Long,
    val user2: Long,
    val id: Long? = null
) : Serializable, MappedEntity<Long>, Jsonable
{
    override fun requireId(): Long
    {
        return id ?: generateId(announcement, user1, user2)
    }

    override fun identifiedClone(id: Long): Identifiable<Long>
    {
        return Conversation(
            announcement,
            user1,
            user2,
            id
        )
    }

    override fun equals(other: Any?): Boolean
    {
        return if(other is Conversation) {
            equals(other)
        } else {
            false
        }
    }

    fun equals(other: Conversation?) : Boolean
    {
        return requireId() == other?.requireId()
    }

    override fun hashCode(): Int
    {
        var result = announcement.hashCode()
        result = 31 * result + user1.hashCode()
        result = 31 * result + user2.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun save(callback: (Boolean) -> Unit)
    {
        throw UnsupportedOperationException("Cannot save conversation")
    }

    override fun delete(callback: (Boolean) -> Unit)
    {
        throw UnsupportedOperationException("Cannot delete conversation")
    }

    override fun toJSON(): String
    {
        val json = JSONObject()

        json.put("announcement", announcement)
        json.put("user1", user1)
        json.put("user2", user2)

        if(id != null)
        {
            json.put("id", id)
        }

        return json.toString()
    }

    companion object
    {
        fun fromJSON(json: String) : Conversation
        {
            val obj = JSONObject(json)

            return Conversation(
                obj.getLong("announcement"),
                obj.getLong("user1"),
                obj.getLong("user2"),
                obj.getLong("id")
            )
        }

        fun generateId(announcement: Long, user1: Long, user2: Long): Long
        {
            val announcementHash = announcement.hashCode()
            val user1Hash = user1.hashCode()
            val user2Hash = user2.hashCode()

            return ((announcementHash * 31) + (user1Hash * 31) + (user2Hash * 31)).toLong()
        }
    }
}
