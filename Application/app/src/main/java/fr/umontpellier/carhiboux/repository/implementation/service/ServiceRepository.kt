package fr.umontpellier.carhiboux.repository.implementation.service

import fr.umontpellier.carhiboux.repository.base.CRUDRepository
import fr.umontpellier.carhiboux.utils.Jsonable
import fr.umontpellier.carhiboux.utils.MappedEntity
import org.json.JSONArray

open class ServiceRepository<T>(private val resource : String, private val parser: (String) -> T) : CRUDRepository<Long, T>
where T : MappedEntity<Long>, T : Jsonable
{
    companion object
    {
        private const val protocol : String = "http"
        private const val domain : String = "192.168.1.15" // TODO Local IP of development computer
        private const val port : String = "3000"

        private const val GET : String = "GET"
        private const val POST : String = "POST"
        private const val PUT : String = "PUT"
        private const val DELETE : String = "DELETE"


        fun buildURL(resource: String) : String
        {
            return "$protocol://$domain:$port/$resource"
        }

        fun buildURL(resource: String, param: String): String
        {
            return buildURL(resource) + "/" + param
        }
    }

    override fun readAll(callback: (List<T>) -> Unit)
    {
        ServiceCallTask(GET, buildURL(resource)) { succeed: Boolean, content: String? ->
            if(succeed)
            {
                val all : MutableList<T> = mutableListOf()
                val result = JSONArray(content)

                for(i in 0 until result.length())
                {
                    val json = result.getJSONObject(i).toString()
                    all.add(parser(json))
                }

                callback.invoke(all)
            }
            else
            {
                callback.invoke(listOf())
            }
        }.start()
    }

    override fun readIf(test: (T) -> Boolean, callback: (List<T>) -> Unit)
    {
        readAll {
            val filtered = mutableListOf<T>()

            for(entity in it)
            {
                if(test.invoke(entity))
                {
                    filtered.add(entity)
                }
            }

            callback.invoke(filtered)
        }
    }

    override fun read(id: Long, callback: (T?) -> Unit)
    {
        ServiceCallTask(GET, buildURL(resource, id.toString())) { succeed: Boolean, content: String? ->
            if(succeed)
            {
                callback.invoke(parser(content!!))
            }
            else
            {
                callback.invoke(null)
            }
        }.start()
    }

    override fun create(entity: T, callback: (Long?) -> Unit)
    {
        ServiceCallTask(POST, buildURL(resource), entity) { succeed: Boolean, content: String? ->
            if(succeed)
            {
                callback.invoke(content?.toLong())
            }
            else
            {
                callback.invoke(null)
            }
        }.start()
    }

    override fun update(id: Long, entity: T, callback: (Boolean) -> Unit)
    {
        ServiceCallTask(PUT, buildURL(resource, id.toString()), entity) { succeed: Boolean, _: String? ->
            callback.invoke(succeed)
        }.start()
    }

    override fun delete(id: Long, callback: (Boolean) -> Unit)
    {
        ServiceCallTask(DELETE, buildURL(resource, id.toString())) { succeed: Boolean, _: String? ->
            callback.invoke(succeed)
        }.start()
    }
}