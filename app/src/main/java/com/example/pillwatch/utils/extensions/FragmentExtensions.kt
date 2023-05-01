package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.example.pillwatch.R
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.extensions.ContextExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import com.google.android.material.bottomnavigation.BottomNavigationView

object FragmentExtensions {
    /**
     *      Extension function to set the value of the `isLoggedIn` preference from SharedPreferences
     */
    fun Fragment.setLoggedInStatus(value: Boolean) {
        requireContext().setLoggedInStatus(value)
    }

    /**
     *      Extension function to get the value of the 'isLoggedIn` preference from SharedPreferences
     */
    fun Fragment.getLoggedInStatus(): Boolean {
        return requireContext().getLoggedInStatus()
    }

    /**
     *      Extension function to set a preference value from SharedPreferences
     */
    fun Fragment.setPreference(key: String, value: String) {
        requireContext().setPreference(key, value)
    }

    /**
     *      Extension function to get a preference value from SharedPreferences by providing a random value of the type of the preference
     */
    fun Fragment.getPreference(key: String, type: Any): Any {
        return requireContext().getPreference(key, type)
    }

    /**
     *      Extension function to check the fragmentId, set the tittle of the toolbar and calls the
     *  isVisible function if the current activity is MainActivity
     *  + set the visibility of the BottomNavigationView
     */

    fun Fragment.toolbarBottomNavVisibility(activity: Activity, fragmentId: Int) {
        val showStatus: Boolean

        val visibleStatus = when (fragmentId) {
            R.id.homeFragment, R.id.medicationFragment, R.id.settingsFragment -> {
                showStatus = true
                View.VISIBLE
            }
            else -> {
                showStatus = false
                View.GONE
            }
        }

        activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            visibleStatus

        if (!showStatus) {
            if (activity is MainActivity) {
                activity.isVisible(showStatus, fragmentId)
            }
        } else {
            if (activity is MainActivity) {
                activity.isVisible(showStatus, fragmentId)
            }
        }
    }
}
