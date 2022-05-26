package fr.umontpellier.carhiboux.repository.base

import fr.umontpellier.carhiboux.utils.MappedEntity

interface CRUDRepository<I, T : MappedEntity<I>> : ReadRepository<I, T>, WriteRepository<I, T>
{
}