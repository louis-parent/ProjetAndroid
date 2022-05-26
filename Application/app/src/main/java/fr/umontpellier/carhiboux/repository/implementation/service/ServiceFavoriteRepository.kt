package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.entity.favorite.Favorite

object ServiceFavoriteRepository : ServiceRepository<Favorite>("favorite", Favorite::fromJSON)