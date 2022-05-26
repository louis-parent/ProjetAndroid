package fr.umontpellier.carhiboux.repository.base

import fr.umontpellier.carhiboux.utils.Identifiable
import fr.umontpellier.carhiboux.utils.MappedEntity
import java.util.function.Consumer

interface ReadRepository<I, T : Identifiable<I>>
{
    fun readAll(callback: (List<T>) -> Unit)
    fun read(id: I, callback: (T?) -> Unit)
    fun readIf(test : (T) -> Boolean, callback: (List<T>) -> Unit)
}