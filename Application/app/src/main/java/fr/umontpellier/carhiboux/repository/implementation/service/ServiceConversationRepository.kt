package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.entity.message.Conversation

object ServiceConversationRepository : ServiceRepository<Conversation>("conversation", Conversation::fromJSON)
{
    override fun create(entity: Conversation, callback: (Long?) -> Unit)
    {
        throw UnsupportedOperationException("No route for conversation creation")
    }

    override fun update(id: Long, entity: Conversation, callback: (Boolean) -> Unit)
    {
        throw UnsupportedOperationException("No route for conversation update")
    }

    override fun delete(id: Long, callback: (Boolean) -> Unit)
    {
        throw UnsupportedOperationException("No route for conversation deletion")
    }
}