package fr.umontpellier.carhiboux.repository.implementation.mock

import fr.umontpellier.carhiboux.bundle.SearchFilters
import fr.umontpellier.carhiboux.entity.alert.Alert

object MockAlertRepository : MockRepository<Alert>(arrayOf(
    Alert(SearchFilters(minPlaces = 5), 1, true, 0),
    Alert(SearchFilters(brand = "Renault", model = "Twingo"), 1, false, 1)
))
