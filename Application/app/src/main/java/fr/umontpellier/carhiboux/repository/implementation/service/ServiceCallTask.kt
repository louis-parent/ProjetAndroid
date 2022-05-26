package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.utils.Jsonable
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.URL

class ServiceCallTask(private val method: String, private val target: String, private val body: Jsonable? = null, private val callback : (Boolean, String?) -> Unit) : Thread()
{
    override fun run()
    {
        try
        {
            val url = URL(target)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.doInput = true

            conn.requestMethod = method

            if (body != null)
            {
                conn.doOutput = true
                conn.setRequestProperty("Content-Type","application/json")
                conn.outputStream.write(body.toJSON().toByteArray())
                conn.outputStream.close()
            }

            conn.connect()

            val response: Int = conn.responseCode

            if (response == 200)
            {
                callback.invoke(true, String(conn.inputStream.readBytes()))
            }
            else
            {
                callback.invoke(false, null)
            }
        }
        catch(exception: SocketException)
        {
            exception.printStackTrace()
            callback.invoke(false, null)
        }
    }
}