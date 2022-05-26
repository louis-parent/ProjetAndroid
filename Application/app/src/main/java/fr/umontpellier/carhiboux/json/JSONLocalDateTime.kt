package fr.umontpellier.carhiboux.json

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object JSONLocalDateTime
{
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun toJSON(value: LocalDateTime) : String
    {
        return formatter.format(value)
    }

    fun fromJSON(json: String): LocalDateTime
    {
        return LocalDateTime.parse(json, formatter)
    }
}