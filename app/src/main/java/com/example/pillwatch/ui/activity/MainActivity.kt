package com.example.pillwatch.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.databinding.ActivityMainBinding
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.viewmodel.MainViewModel
import com.example.pillwatch.viewmodel.factory.MainViewModelFactory
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfigurationNavBottom: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        binding = com.example.pillwatch.databinding.ActivityMainBinding.inflate(layoutInflater)


        val userDao = AppDatabase.getInstance(application).userDao
        val medsDao = AppDatabase.getInstance(application).medsDao
        val metadataDao = AppDatabase.getInstance(application).metadataDao

        val viewModelFactory = MainViewModelFactory(medsDao, metadataDao, userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContentView(binding.root)
        binding.apply {

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            setSupportActionBar(toolbar)
            toolbar.title = getPreference("username")
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

            drawerView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_logout -> {
                        navController.navigate(R.id.loadingFragment)
                        viewModel.logout()
                    }

                    R.id.nav_clean -> {
                        navController.navigate(R.id.loadingFragment)
                        viewModel.clear()
                    }

                    else -> {
                        navController.popBackStack(viewModel.currentFragmentId.value!!, true)
                        navController.navigate(item.itemId)
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            hamburgerIcon.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }

            bottomNavigationView.setupWithNavController(navController)

            appBarConfigurationNavBottom = AppBarConfiguration(
                setOf(R.id.homeFragment, R.id.medicationFragment, R.id.settingsFragment)
            )
            setupActionBarWithNavController(navController, appBarConfigurationNavBottom)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun isVisible(bool: Boolean, fragmentId: Int) {
        viewModel.setCurrentFragmentId(fragmentId)
        if (bool) {
            binding.toolbarFrame.visibility = View.VISIBLE
        } else {
            binding.toolbarFrame.visibility = View.INVISIBLE
        }
    }

}