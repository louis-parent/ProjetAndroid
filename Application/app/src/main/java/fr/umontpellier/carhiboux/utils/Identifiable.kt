package fr.umontpellier.carhiboux.utils

interface Identifiable<T> : Cloneable
{
    fun requireId() : T
    fun identifiedClone(id: T) : Identifiable<T>
}