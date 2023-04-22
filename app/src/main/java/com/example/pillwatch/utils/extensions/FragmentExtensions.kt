package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pillwatch.R
import com.example.pillwatch.ui.activity.MainActivity
import com.example.pillwatch.utils.extensions.ContextExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import com.google.android.material.bottomnavigation.BottomNavigationView

object FragmentExtensions {

    fun Fragment.setLoggedInStatus(value: Boolean) {
        requireContext().setLoggedInStatus(value)
    }

    fun Fragment.getLoggedInStatus(): Boolean {
        return requireContext().getLoggedInStatus()
    }

    fun Fragment.setPreference(key: String, value: String) {
        requireContext().setPreference(key, value)
    }

    fun Fragment.getPreference(key: String, type: Any): Any {
        return requireContext().getPreference(key, type)
    }

    fun Fragment.navBarVisibilityState(activity: Activity, fragmentId: Int) {
        val visibleStatus = when (fragmentId) {
            R.id.homeFragment -> View.VISIBLE
            R.id.medicationFragment -> View.VISIBLE
            R.id.settingsFragment -> View.VISIBLE
            else -> View.GONE
        }

        activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            visibleStatus
    }

    fun Fragment.toolbarVisibilityState(activity: Activity, fragmentId: Int) {
        val showStatus = when (fragmentId) {
            R.id.homeFragment -> true
            R.id.medicationFragment -> true
            R.id.settingsFragment -> true
            else -> false
        }

        val username = getPreference("username", "").toString()
        if (!showStatus) {
            if(activity is MainActivity){
                activity.isVisible(showStatus, fragmentId)
            }
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = username
            if(activity is MainActivity){
                activity.isVisible(showStatus, fragmentId)
            }
        }
    }
}
