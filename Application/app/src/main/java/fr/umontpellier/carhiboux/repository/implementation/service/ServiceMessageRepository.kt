package fr.umontpellier.carhiboux.repository.implementation.service

import android.os.Build
import androidx.annotation.RequiresApi
import fr.umontpellier.carhiboux.entity.message.Message

@RequiresApi(Build.VERSION_CODES.O)
object ServiceMessageRepository : ServiceRepository<Message>("message", Message::fromJSON)