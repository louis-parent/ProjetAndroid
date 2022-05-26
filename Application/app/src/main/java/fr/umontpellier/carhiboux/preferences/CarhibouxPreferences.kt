package fr.umontpellier.carhiboux.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import fr.umontpellier.carhiboux.entity.user.User

object CarhibouxPreferences
{
    private const val PREFERENCES_KEY = "CarhibouxPreferences"
    private const val USER_KEY = "user"

    private lateinit var preferences : SharedPreferences

    fun init(context: Context)
    {
        preferences = context.getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)
    }

    fun setUser(user: User)
    {
        setUser(user.toJSON())
    }

    private fun setUser(user: String)
    {
        val editor = preferences.edit()
        editor.putString(USER_KEY, user)
        editor.apply()
    }

    fun getUser(): User?
    {
        val json = preferences.getString(USER_KEY, null)

        return if(json != null) {
            User.fromJSON(json)
        } else {
            null
        }
    }

    fun hasUser() : Boolean
    {
        return preferences.contains(USER_KEY)
    }

    fun clearUser()
    {
        val editor = preferences.edit()
        editor.remove(USER_KEY)
        editor.apply()
    }
}