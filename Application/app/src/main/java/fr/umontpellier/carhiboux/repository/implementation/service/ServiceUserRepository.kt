package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.entity.user.User

object ServiceUserRepository : ServiceRepository<User>("user", User::fromJSON)
