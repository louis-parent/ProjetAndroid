package fr.umontpellier.carhiboux.repository.implementation.mock

import fr.umontpellier.carhiboux.entity.favorite.Favorite

object MockFavoriteRepository : MockRepository<Favorite>(arrayOf(
    Favorite(1, 0, 0)
))
