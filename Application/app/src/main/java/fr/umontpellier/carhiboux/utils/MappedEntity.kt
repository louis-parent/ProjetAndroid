package fr.umontpellier.carhiboux.utils

interface MappedEntity<T> : Identifiable<T>
{
    fun save(callback: (Boolean) -> Unit)
    fun delete(callback: (Boolean) -> Unit)
}