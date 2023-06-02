package com.example.pillwatch.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.R.*
import com.example.pillwatch.alarms.AlarmSchedulerWorker
import com.example.pillwatch.databinding.ActivityMainBinding
import com.example.pillwatch.ui.home.HomeFragment
import com.example.pillwatch.ui.splash.SplashActivity
import com.example.pillwatch.user.UserManager
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.extensions.ContextExtensions.dismissProgressDialog
import com.example.pillwatch.utils.extensions.ContextExtensions.isInternetConnected
import com.example.pillwatch.utils.extensions.ContextExtensions.showProgressDialog
import com.example.pillwatch.utils.extensions.ContextExtensions.snackbar
import com.example.pillwatch.utils.extensions.ContextExtensions.toastTop
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfigurationNavBottom: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var medsAPIViewModel: MedsAPIViewModel

    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = (application as PillWatchApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)

        Timber.plant(Timber.DebugTree())
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        scheduleAlarmSchedulerWorker(this, userManager.id)

        if (!isInternetConnected()) {
            toastTop("No internet connection.")
        }

        setThemeAccordingToPreference()

        if (userManager.justLoggedInStatus) {
            mainViewModel.getDataFromCloud()
        }

        mainViewModel.refreshUI.observe(this) {
            if (it != null && it) {
                refreshHomeFragment()
            }
        }

        // Firebase Messaging token retrieval
        val firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.tag("TOKEN").d(token)
            }
        }

        binding.apply {

            /**
             *     NavController initialization
             */
            val navHostFragment =
                supportFragmentManager.findFragmentById(id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            /**
             *      Toolbar initialization
             *      Toolbar title changed to the username saved in SharedPreferences
             */
            setToolbarUsername()
            setSupportActionBar(toolbar)

            bottomNavigationView.setupWithNavController(navController)

            appBarConfigurationNavBottom = AppBarConfiguration(
                setOf(id.homeFragment, id.medicationFragment, id.settingsFragment)
            )
            setupActionBarWithNavController(navController, appBarConfigurationNavBottom)

            /**
             *      DrawerView ActionToggle initialization and setup
             */
            supportActionBar?.setDisplayShowTitleEnabled(false)
            actionBarDrawerToggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                toolbar,
                string.nav_open,
                string.nav_close
            )
            actionBarDrawerToggle.isDrawerIndicatorEnabled = false
            drawerLayout.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()

            /**
             *      HamburgerIcon click listener for opening and closing the drawer
             */
            openDrawer.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            /**
             *      DrawerView navigation options and the logic for the options
             */
            drawerView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    id.nav_logout -> {
                        logout()
                    }

                    id.nav_clean -> {
                        navController.navigate(id.homeFragment)
                        mainViewModel.clear()
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    id.nav_update -> {
                        val progressDialog = showProgressDialog("Checking for updates")
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                medsAPIViewModel.getMedsDataFromAPI(false)
                            }
                            dismissProgressDialog(
                                progressDialog,
                                medsAPIViewModel.updateDialogTitle.value!!,
                                medsAPIViewModel.updateMessage.value!!
                            )
                        }
                    }

                    else -> {
                        navController.popBackStack(mainViewModel.currentFragmentId.value!!, true)
                        navController.navigate(item.itemId)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
                true
            }

            mainViewModel.getUserRole(userManager.id)

            mainViewModel.userRole.observe(this@MainActivity) {
                val navCleanItem = drawerView.menu.findItem(id.nav_clean)
                navCleanItem.isVisible = it == Role.ADMIN
            }

        }

        mainViewModel.showToast()

        mainViewModel.showNotification.observe(this) {
            if (it != null && it) {
                showMessage()
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Close the navigation drawer if it is open
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mainViewModel.currentFragmentId.value?.let {
                when (mainViewModel.currentFragmentId.value) {
                    id.medPageFragment -> {
                        when (mainViewModel.previousFragmentId.value) {
                            id.medicationFragment -> {
                                navController.navigate(
                                    id.medicationFragment
                                )
                            }

                            else -> {
                                navController.navigate(id.homeFragment)
                            }
                        }
                    }

                    else -> super.onBackPressed()
                }
            }
        }
    }

    private fun refreshHomeFragment() {
        val currentFragmentId = mainViewModel.currentFragmentId.value

        if (currentFragmentId == id.homeFragment) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(id.nav_host_fragment) as? NavHostFragment
            val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()

            if (currentFragment is HomeFragment) {
                currentFragment.recyclerViewImplementation()
            }
        }
    }

    private fun showMessage() {
        val messageIds = arrayOf(
            string.healthcare,
            string.interaction,
            string.medical_advice
        )

        // Get the index of the last displayed message (defaulting to -1 if not found)
        val lastIndex = userManager.messageIndex

        // Compute the index of the next message
        val nextIndex = (lastIndex + 1) % messageIds.size

        // Get the string for the next message
        val nextMessage = getString(messageIds[nextIndex])

        binding.root.snackbar(nextMessage, attr.colorWarning, 10000, 110)

        // Store the index of the next message
        userManager.setMessageIndex(nextIndex)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Sets the current fragment ID in the MainViewModel and updates the visibility of the ToolbarFrame and BottomNavigationView.
     *
     * @param bool Determines whether the ToolbarFrame and BottomNavigationView should be visible or not based on the provided boolean value.
     */
    fun navBarToolbarBottomNav(bool: Boolean, fragmentId: Int) {
        mainViewModel.setPreviousFragment(
            mainViewModel.currentFragmentId.value ?: mainViewModel.previousFragmentId.value!!
        )
        mainViewModel.setCurrentFragmentId(fragmentId)
        if (bool) {
            binding.toolbarFrame.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.VISIBLE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.toolbarFrame.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun setThemeAccordingToPreference() {
        when (userManager.theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    /**
     * Retrieves the ID of the previous fragment from the MainViewModel.
     *
     * @return The ID of the previous fragment.
     */
    fun getPreviousFragment(): Int? {
        return mainViewModel.previousFragmentId.value
    }

    private fun scheduleAlarmSchedulerWorker(context: Context, userId: String) {
        val workManager = WorkManager.getInstance(context)

        Timber.d("WORKER USER ID = $userId")
        val alarmSchedulerWorkerRequest =
            PeriodicWorkRequestBuilder<AlarmSchedulerWorker>(1, TimeUnit.HOURS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "AlarmSchedulerWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            alarmSchedulerWorkerRequest
        )

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<AlarmSchedulerWorker>()
            .build()

        workManager.enqueue(oneTimeWorkRequest)
    }

    fun logout() {
        mainViewModel.logout()
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        val intent = Intent(this@MainActivity, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setToolbarUsername() {
        binding.toolbarUsername.text = userManager.username
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "theme") {
            setThemeAccordingToPreference()
        }
    }
}