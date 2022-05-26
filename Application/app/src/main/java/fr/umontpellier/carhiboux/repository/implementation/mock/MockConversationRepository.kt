package fr.umontpellier.carhiboux.repository.implementation.mock

import fr.umontpellier.carhiboux.entity.message.Conversation
import fr.umontpellier.carhiboux.repository.base.ReadRepository

object MockConversationRepository : ReadRepository<Long, Conversation>
{
    override fun readAll(callback: (List<Conversation>) -> Unit)
    {
        MockMessageRepository.readAll { messages ->
            val conversations : MutableList<Conversation> = mutableListOf()

            for(message in messages)
            {
                val conversation = Conversation(message.announcement, message.source, message.destination)

                if(!conversations.contains(conversation))
                {
                    conversations.add(conversation)
                }
            }

            callback.invoke(conversations)
        }
    }

    override fun read(id: Long, callback: (Conversation?) -> Unit)
    {
        readAll { conversations ->
            var found : Conversation? = null

            for (conversation in conversations)
            {
                if (conversation.requireId() == id)
                {
                    found = conversation
                }
            }

            callback.invoke(found)
        }
    }

    override fun readIf(test: (Conversation) -> Boolean, callback: (List<Conversation>) -> Unit)
    {
        readAll {
            val conversations : MutableList<Conversation> = mutableListOf()

            for(conversation in it)
            {
                if(test(conversation))
                {
                    conversations.add(conversation)
                }
            }

            callback.invoke(conversations)
        }
    }
}