package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.entity.alert.Alert

object ServiceAlertRepository: ServiceRepository<Alert>("alert", Alert::fromJSON)