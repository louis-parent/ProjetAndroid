package fr.umontpellier.carhiboux.repository.base

import fr.umontpellier.carhiboux.utils.MappedEntity

interface WriteRepository<I, T : MappedEntity<I>>
{
    fun create(entity: T, callback: (I?) -> Unit)
    fun update(id: I, entity: T, callback: (Boolean) -> Unit)
    fun delete(id: I, callback: (Boolean) -> Unit)
}