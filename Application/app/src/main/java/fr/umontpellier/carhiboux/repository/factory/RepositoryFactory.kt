package fr.umontpellier.carhiboux.repository.factory

import fr.umontpellier.carhiboux.entity.alert.Alert
import fr.umontpellier.carhiboux.entity.announcement.Announcement
import fr.umontpellier.carhiboux.entity.favorite.Favorite
import fr.umontpellier.carhiboux.entity.message.Conversation
import fr.umontpellier.carhiboux.entity.message.Message
import fr.umontpellier.carhiboux.entity.notification.Notification
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.repository.base.CRUDRepository
import fr.umontpellier.carhiboux.repository.base.ReadRepository
import fr.umontpellier.carhiboux.repository.implementation.mock.*
import fr.umontpellier.carhiboux.repository.implementation.service.*

interface RepositoryFactory
{
    fun getAlertRepository() : CRUDRepository<Long, Alert>
    fun getAnnouncementRepository() : CRUDRepository<Long, Announcement>
    fun getConversationRepository() : ReadRepository<Long, Conversation>
    fun getFavoriteRepository() : CRUDRepository<Long, Favorite>
    fun getMessageRepository() : CRUDRepository<Long, Message>
    fun getNotificationRepository() : CRUDRepository<Long, Notification>
    fun getUserRepository() : CRUDRepository<Long, User>

    companion object
    {
        fun getPreferredFactory() : RepositoryFactory
        {
            // TODO Choice of the factory to use
            return getServiceFactory() // Service
            //return getMockFactory() // Mock
        }

        fun getMockFactory() : RepositoryFactory
        {
            return object : RepositoryFactory {
                override fun getAlertRepository(): CRUDRepository<Long, Alert>
                {
                    return MockAlertRepository
                }

                override fun getAnnouncementRepository(): CRUDRepository<Long, Announcement>
                {
                    return MockAnnouncementRepository
                }

                override fun getConversationRepository(): ReadRepository<Long, Conversation>
                {
                    return MockConversationRepository
                }

                override fun getFavoriteRepository(): CRUDRepository<Long, Favorite>
                {
                    return MockFavoriteRepository
                }

                override fun getMessageRepository(): CRUDRepository<Long, Message>
                {
                    return MockMessageRepository
                }

                override fun getNotificationRepository(): CRUDRepository<Long, Notification>
                {
                    return MockNotificationRepository
                }

                override fun getUserRepository(): CRUDRepository<Long, User>
                {
                    return MockUserRepository
                }
            }
        }

        fun getServiceFactory() : RepositoryFactory
        {
            return object : RepositoryFactory {
                override fun getAlertRepository(): CRUDRepository<Long, Alert>
                {
                    return ServiceAlertRepository
                }

                override fun getAnnouncementRepository(): CRUDRepository<Long, Announcement>
                {
                    return ServiceAnnouncementRepository
                }

                override fun getConversationRepository(): ReadRepository<Long, Conversation>
                {
                    return ServiceConversationRepository
                }

                override fun getFavoriteRepository(): CRUDRepository<Long, Favorite>
                {
                    return ServiceFavoriteRepository
                }

                override fun getMessageRepository(): CRUDRepository<Long, Message>
                {
                    return ServiceMessageRepository
                }

                override fun getNotificationRepository(): CRUDRepository<Long, Notification>
                {
                    return ServiceNotificationRepository
                }

                override fun getUserRepository(): CRUDRepository<Long, User>
                {
                    return ServiceUserRepository
                }
            }
        }
    }
}