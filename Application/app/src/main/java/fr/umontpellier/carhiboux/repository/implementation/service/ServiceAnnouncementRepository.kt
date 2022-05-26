package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.entity.announcement.Announcement

object ServiceAnnouncementRepository : ServiceRepository<Announcement>("announcement", Announcement::fromJSON)