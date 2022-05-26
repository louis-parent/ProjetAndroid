package fr.umontpellier.carhiboux.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import fr.umontpellier.carhiboux.preferences.CarhibouxPreferences
import fr.umontpellier.carhiboux.R
import fr.umontpellier.carhiboux.bundle.BundleKeys
import fr.umontpellier.carhiboux.bundle.SearchFilters
import fr.umontpellier.carhiboux.entity.user.ParticularUser
import fr.umontpellier.carhiboux.entity.user.ProfessionalUser
import fr.umontpellier.carhiboux.entity.user.User
import fr.umontpellier.carhiboux.notification.CarhibouxNotificationSystem
import fr.umontpellier.carhiboux.notification.NotificationCheckWorker


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLocker
{
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navController : NavController
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        CarhibouxNotificationSystem.initChannel(this)
        CarhibouxPreferences.init(this)
        NotificationCheckWorker.enqueue(this)

        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        drawerLayout = findViewById(R.id.main_drawer)
        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            findViewById(R.id.toolbar),
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        findViewById<NavigationView>(R.id.main_navigation).setNavigationItemSelectedListener(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerToggle.syncState()
        updateMenuVisibility()
    }

    override fun setDrawerEnabled(enabled: Boolean)
    {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        drawerLayout.closeDrawer(GravityCompat.START)

        when(item.itemId)
        {
            R.id.home -> {
                navController.navigate(R.id.to_announcement_list)
            }

            R.id.login -> {
                navController.navigate(R.id.to_login)
            }

            R.id.my_profile -> {
                connectedNavigation(R.id.to_profile)
            }

            R.id.my_announcements -> {
                connectedNavigation(R.id.to_announcement_list, bundleOf(BundleKeys.FILTERS to SearchFilters(announcer = getConnectedUser()?.id)))
            }

            R.id.my_notifications -> {
                connectedNavigation(R.id.to_notification_list)
            }

            R.id.my_alerts -> {
                connectedNavigation(R.id.to_alert_list)
            }

            R.id.logout -> {
                disconnectLawyer()
                navController.navigate(R.id.to_announcement_list)
            }

            R.id.my_chat -> {
                connectedNavigation(R.id.to_chat_list)
            }

            R.id.my_statistics -> {
                connectedNavigation(R.id.to_statistics)
            }
        }

        return true
    }

    fun getConnectedUser(): User?
    {
        return CarhibouxPreferences.getUser()
    }

    fun isConnectedUser() : Boolean
    {
        return CarhibouxPreferences.hasUser()
    }

    private fun isPremium() : Boolean
    {
        return if(isConnectedUser()) {
            val user = CarhibouxPreferences.getUser()

            if(user?.isProfessional == true) {
                (user as ProfessionalUser).isPremium
            } else {
                false
            }
        } else {
            false
        }
    }

    fun connectUser(user: ProfessionalUser)
    {
        CarhibouxPreferences.setUser(user)
        updateMenuVisibility()
    }

    fun connectUser(user: ParticularUser)
    {
        CarhibouxPreferences.setUser(user)
        updateMenuVisibility()
    }

    private fun disconnectLawyer()
    {
        CarhibouxPreferences.clearUser()
        updateMenuVisibility()
    }

    private fun connectedNavigation(target : Int, arguments : Bundle? = null)
    {
        if(isConnectedUser())
        {
            val bundle = bundleOf(BundleKeys.USER to getConnectedUser()?.requireId())
            arguments?.putAll(bundle)

            navController.navigate(target, arguments ?: bundle)
        }
        else
        {
            navController.navigate(R.id.to_login, bundleOf("redirect" to (target to arguments)))
        }
    }

    private fun updateMenuVisibility()
    {
        runOnUiThread {
            val unconnectedOnlyItems = arrayOf(R.id.login)
            val connectedAccessOnlyItems = arrayOf(R.id.my_profile, R.id.my_announcements, R.id.my_notifications, R.id.my_chat, R.id.my_alerts, R.id.logout)
            val premiumAccessOnlyItems = arrayOf(R.id.my_statistics)

            for(unconnectedOnlyItem in unconnectedOnlyItems)
            {
                val item = findViewById<NavigationView>(R.id.main_navigation).menu.findItem(unconnectedOnlyItem)
                item.isVisible = !isConnectedUser()
                item.isEnabled = !isConnectedUser()
            }

            for(connectedAccessOnlyItem in connectedAccessOnlyItems)
            {
                val item = findViewById<NavigationView>(R.id.main_navigation).menu.findItem(connectedAccessOnlyItem)
                item.isVisible = isConnectedUser()
                item.isEnabled = isConnectedUser()
            }

            for(premiumAccessOnlyItem in premiumAccessOnlyItems)
            {
                val item = findViewById<NavigationView>(R.id.main_navigation).menu.findItem(premiumAccessOnlyItem)
                item.isVisible = isPremium()
                item.isEnabled = isPremium()
            }
        }
    }
}