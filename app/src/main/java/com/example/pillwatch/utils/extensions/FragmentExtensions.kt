package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.example.pillwatch.R
import com.example.pillwatch.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object FragmentExtensions {

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
