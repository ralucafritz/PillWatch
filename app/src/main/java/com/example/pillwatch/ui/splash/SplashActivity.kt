package com.example.pillwatch.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.ActivitySplashBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.welcome.WelcomeFragment
import com.example.pillwatch.user.UserManager
import javax.inject.Inject

class SplashActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var userManager: UserManager

    @Inject
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userManager = (application as PillWatchApplication).appComponent.userManager()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainSplash.visibility = View.VISIBLE
        checkLoggedIn()
    }

    fun checkLoggedIn(){
        if (!userManager.isUserLoggedIn()) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_holder, WelcomeFragment())
                .commit()
            binding.fragmentHolder.visibility = View.VISIBLE
            binding.mainSplash.visibility = View.INVISIBLE
        } else {
            userManager.userComponent!!.inject(this)
            binding.mainSplash.visibility = View.VISIBLE
            binding.fragmentHolder.visibility = View.INVISIBLE
            binding.viewModel = viewModel
            viewModel.setMessage()
            viewModel.navigationCheck.observe(this) {
                if(it!=null && it) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}