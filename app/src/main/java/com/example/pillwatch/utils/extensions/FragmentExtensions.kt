package com.example.pillwatch.utils.extensions

import androidx.fragment.app.Fragment
import com.example.pillwatch.utils.extensions.ContextExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference

object FragmentExtensions {

    fun Fragment.setLoggedInStatus(value: Boolean) {
        requireContext().setLoggedInStatus(value)
    }

    fun Fragment.getLoggedInStatus() : Boolean{
        return requireContext().getLoggedInStatus()
    }

    fun Fragment.setPreference(key: String, value: String) {
        requireContext().setPreference(key, value)
    }

    fun Fragment.getPreference(key: String, type: Any) : Any{
        return requireContext().getPreference(key, type)
    }
}