package com.example.pillwatch.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pillwatch.R
import com.example.pillwatch.data.source.local.AppDatabase
import com.example.pillwatch.databinding.ActivityMainBinding
import com.example.pillwatch.utils.extensions.ContextExtensions.dismissProgressDialog
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.showProgressDialog
import com.example.pillwatch.ui.MedsViewModel
import com.example.pillwatch.ui.MedsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfigurationNavBottom: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var medsViewModel: MedsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        binding = ActivityMainBinding.inflate(layoutInflater)

        /**
         *    DatabaseDao access
         */
        val userDao = AppDatabase.getInstance(application).userDao
        val medsDao = AppDatabase.getInstance(application).medsDao
        val metadataDao = AppDatabase.getInstance(application).metadataDao
        val userMedsDao = AppDatabase.getInstance(application).userMedsDao
        val alarmDao = AppDatabase.getInstance(application).alarmDao
        val medsLogDao = AppDatabase.getInstance(application).medsLogDao

        /**
         *    ViewModels initializations
         */
        val mainViewModelFactory = MainViewModelFactory(medsDao, metadataDao, userDao, userMedsDao, medsLogDao, alarmDao,application)
        mainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]

        val medsViewModelFactory = MedsViewModelFactory(medsDao, metadataDao, application)
        medsViewModel = ViewModelProvider(this, medsViewModelFactory)[MedsViewModel::class.java]

        setContentView(binding.root)
        binding.apply {

            /**
             *     NavController initialization
             */
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            /**
             *      Toolbar initialization
             *      Toolbar title changed to the username saved in SharedPreferences
             */
            binding.toolbarUsername.text = getPreference("username")
            setSupportActionBar(toolbar)

            /**
             *      This function sets the @currentFragmentId MutableLiveData value in the ViewModel
             *      @param bool is used to determine if the ToolbarFrame should be visible or not
             */
            bottomNavigationView.setupWithNavController(navController)

            appBarConfigurationNavBottom = AppBarConfiguration(
                setOf(R.id.homeFragment, R.id.medicationFragment, R.id.settingsFragment)
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
                R.string.nav_open,
                R.string.nav_close
            )
            drawerLayout.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            /**
             *      HamburgerIcon click listener for opening and closing the drawer
             */
            hamburgerIcon.setOnClickListener {
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
                    R.id.nav_logout -> {
                        navController.navigate(R.id.loadingFragment)
                        mainViewModel.logout()
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.nav_clean -> {
                        navController.navigate(R.id.loadingFragment)
                        mainViewModel.clear()
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    R.id.nav_update -> {
                        val progressDialog = showProgressDialog("Checking for updates")
                        lifecycleScope .launch {
                            withContext(Dispatchers.IO) {medsViewModel.getMedsDataFromAPI()
                            }
                            dismissProgressDialog(progressDialog, medsViewModel.updateDialogTitle.value!!, medsViewModel.updateMessage.value!!)
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
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     *      This function sets the @currentFragmentId MutableLiveData value in the ViewModel
     *      @param bool is used to determine if the ToolbarFrame should be visible or not
     */
    fun isVisible(bool: Boolean, fragmentId: Int) {
        mainViewModel.setCurrentFragmentId(fragmentId)
        if (bool) {
            binding.toolbarFrame.visibility = View.VISIBLE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            binding.toolbarFrame.visibility = View.GONE
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }


}