package fr.umontpellier.carhiboux.notification

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import fr.umontpellier.carhiboux.entity.enumeration.NotificationType
import fr.umontpellier.carhiboux.preferences.CarhibouxPreferences
import fr.umontpellier.carhiboux.repository.factory.RepositoryFactory
import java.util.concurrent.TimeUnit

class NotificationCheckWorker(context: Context, params: WorkerParameters): Worker(context, params)
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result
    {
        CarhibouxPreferences.init(applicationContext)

        val user = CarhibouxPreferences.getUser()

        if(user != null)
        {
            RepositoryFactory.getPreferredFactory().getNotificationRepository().readIf({
                !it.notified && it.user == user.requireId()
            }) { notifications ->
                for(notification in notifications)
                {
                    if(notification.type == NotificationType.MESSAGE)
                    {
                        RepositoryFactory.getPreferredFactory().getConversationRepository().read(notification.data) { conversation ->
                            RepositoryFactory.getPreferredFactory().getMessageRepository().readIf ({
                                it.announcement == conversation!!.announcement && it.destination == user.requireId() && it.source == (if (conversation.user1 == user.requireId()) conversation.user2 else conversation.user1)
                            }) { messages ->
                                val lastMessage = messages.maxByOrNull {
                                    it.dateTime
                                }!!

                                CarhibouxNotificationSystem.createNotification(notification.type, lastMessage.text, applicationContext)
                            }
                        }
                    }
                    else if(notification.type == NotificationType.ALERT)
                    {
                        RepositoryFactory.getPreferredFactory().getAnnouncementRepository().read(notification.data) {
                            CarhibouxNotificationSystem.createNotification(notification.type, it?.title().toString(), applicationContext)
                        }
                    }
                }
            }

            return Result.success()
        }
        else
        {
            return Result.failure()
        }
    }

    companion object
    {
        private const val NAME = "fr.umontpellier.carhiboux.notification.worker"
        private const val repeatInterval = 15L
        private const val flexInterval = 5L

        private fun getWorkRequest() : PeriodicWorkRequest
        {
            return PeriodicWorkRequest.Builder(NotificationCheckWorker::class.java, repeatInterval, TimeUnit.MINUTES, flexInterval, TimeUnit.MINUTES).build()
        }

        fun enqueue(context: Context)
        {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.KEEP, getWorkRequest())
        }
    }
}