package fr.umontpellier.carhiboux.repository.implementation.mock

import fr.umontpellier.carhiboux.entity.enumeration.Civility
import fr.umontpellier.carhiboux.entity.user.ParticularUser
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.entity.util.Address

object MockUserRepository : MockRepository<User>(arrayOf(
    ProfessionalUser("contact@corp.com", "1234", Address("3 rue de la Comédie", "34000", "Montpellier", "France"), "0400000000", "Corp", "0000000000000000", true, 0),
    ParticularUser("jean.dupont@email.com", "1234", Address("1 rue de la Comédie", "34000", "Montpellier", "France"), "0600000000", Civility.MISTER, "Jean", "Dupont", 1)
))
