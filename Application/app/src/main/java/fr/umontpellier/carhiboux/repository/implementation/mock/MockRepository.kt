package fr.umontpellier.carhiboux.repository.implementation.mock

import fr.umontpellier.carhiboux.repository.base.CRUDRepository
import fr.umontpellier.carhiboux.utils.MappedEntity

abstract class MockRepository<T : MappedEntity<Long>>(entities: Array<T>) : CRUDRepository<Long, T>
{
    private val entities : MutableList<T> = entities.toMutableList()

    override fun readAll(callback: (List<T>) -> Unit)
    {
        callback.invoke(entities)
    }

    override fun read(id: Long, callback: (T?) -> Unit)
    {
        var found : T? = null

        for(entity in entities)
        {
            if(entity.requireId() == id)
            {
                found = entity
            }
        }

        callback.invoke(found)
    }

    override fun readIf(test: (T) -> Boolean, callback: (List<T>) -> Unit)
    {
        val filtered : MutableList<T> = mutableListOf()

        for(entity in entities)
        {
            if(test(entity))
            {
                filtered.add(entity)
            }
        }

        callback.invoke(filtered)
    }

    override fun create(entity: T, callback: (Long?) -> Unit)
    {
        nextId { id ->
            entities.add(entity.identifiedClone(id) as T)
            callback.invoke(id)
        }
    }

    override fun update(id: Long, entity: T, callback: (Boolean) -> Unit)
    {
        val index = entities.indexOfFirst {
            it.requireId() == id
        }
        entities[index] = entity

        callback.invoke(true)
    }

    private fun nextId(callback: (Long) -> Unit)
    {
        readAll { entities ->
            callback.invoke(entities.maxOfOrNull {
                it.requireId()
            } ?: 0)
        }
    }

    override fun delete(id: Long, callback: (Boolean) -> Unit)
    {
        val toRemove = mutableListOf<T>()

        for(entity in entities)
        {
            if(entity.requireId() == id)
            {
                toRemove.add(entity)
            }
        }

        for(entity in toRemove)
        {
            entities.remove(entity)
        }

        callback.invoke(toRemove.isNotEmpty())
    }
}