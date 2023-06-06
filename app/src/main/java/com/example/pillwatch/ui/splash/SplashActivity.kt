package com.example.pillwatch.ui.splash

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.ActivitySplashBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.main.MedsAPIViewModel
import com.example.pillwatch.ui.splash.welcome.WelcomeFragment
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var userManager: UserManager

    @Inject
    lateinit var viewModel: SplashViewModel

    @Inject
    lateinit var medsAPIViewModel: MedsAPIViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = (application as PillWatchApplication).appComponent.userManager()
        setThemeAccordingToPreference()
        setLanguageAccordingToPreference()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainSplash.visibility = View.VISIBLE

        // Check if the user is logged in
        checkLoggedIn()
    }

    /**
     * Check if the user is logged in.
     * If logged in, retrieve user data from the database and start the main activity.
     * If not logged in, show the welcome fragment.
     */
    fun checkLoggedIn() {
        if (!userManager.isUserLoggedIn()) {
            userNotLoggedIn()
        } else {
            // Inject dependencies
            userManager.userComponent!!.inject(this)
            binding.viewModel = viewModel
            viewModel.checkUserExistsInDb()
            viewModel.userInDb.observe(this) {
                if (it != null && it) {
                    // User exists in the database
                    binding.mainSplash.visibility = View.VISIBLE
                    binding.fragmentHolder.visibility = View.INVISIBLE
                    viewModel.setMessage(resources.getString(R.string.welcome))
                    // Set welcome message
                    viewModel.welcomeMessage.observe(this) { welcomeMessage ->
                        binding.welcomeMessage.text = welcomeMessage
                    }
                    // Observe UI status for data loading
                    viewModel.uiStatus.observe(this) { uiStatus ->
                        if (uiStatus != null && uiStatus)
                            lifecycleScope.launch {
                                    medsAPIViewModel.getMedsDataFromAPI()
                                    viewModel.navigationStart()
                            }
                    }
                    // Observe navigation check for starting the main activity
                    viewModel.navigationCheck.observe(this) { navigationCheck ->
                        if (navigationCheck != null && navigationCheck) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            viewModel.navigationComplete()
                            finish()
                        }
                    }
                } else {
                    userNotLoggedIn()
                }
            }
        }
    }

    /**
     * Show the welcome fragment when the user is not logged in.
     */
    private fun userNotLoggedIn() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_holder, WelcomeFragment())
            .commit()
        binding.fragmentHolder.visibility = View.VISIBLE
        binding.mainSplash.visibility = View.INVISIBLE
    }

    /**
     * Sets the theme of the activity based on the saved theme preference.
     */
    private fun setThemeAccordingToPreference() {
        when (userManager.theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    /**
     * Sets the language of the activity based on the saved preference.
     */
    private fun setLanguageAccordingToPreference() {
//        (application as PillWatchApplication).setLocale(userManager.language)
    }
}