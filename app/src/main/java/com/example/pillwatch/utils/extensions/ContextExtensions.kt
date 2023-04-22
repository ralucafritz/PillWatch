package com.example.pillwatch.utils.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext

object ContextExtensions {
    /**
     *      Extension function to set the value of the `isLoggedIn` preference from SharedPreferences
     */
    fun Context.setLoggedInStatus(value: Boolean) {
        SharedPreferencesExtensions.setPreference(this, "isLoggedIn", value)
    }
    /**
     *      Extension function to get the value of the 'isLoggedIn` preference from SharedPreferences
     */
    fun Context.getLoggedInStatus() : Boolean {
        return SharedPreferencesExtensions.getPreference(this, "isLoggedIn", false)
    }
    /**
     *      Extension function to set a preference value from SharedPreferences
     */
    fun Context.setPreference(key: String, value: Any) {
        SharedPreferencesExtensions.setPreference(this, key, value)
    }
    /**
     *      Extension function to get a preference value from SharedPreferences by providing a random value of the type of the preference
     */
    fun Context.getPreference(key: String, type: Any) : Any {
        return SharedPreferencesExtensions.getPreference(this, key, type)
    }
    /**
     *      Extension function to get a string preference value from SharedPreferences
     */
    fun Context.getPreference(key: String) : String {
        return SharedPreferencesExtensions.getPreference(this, key, "")
    }
    /**
     *      Extension function to check if the device is currently connected to the internet
     */
    fun Context.isInternetConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    /**
     *      Extension function to show a simple alert dialog
     */
    fun Context.showAlert(message: String, title: String = "") {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.create()

        builder.show()
    }

    fun Context.showProgressDialog(
        title: String,
        isCancelable: Boolean = false
    ): AlertDialog {
        val progressDialog = AlertDialog.Builder(this)
            .setView(ProgressBar(this))
            .setTitle(title)
            .setMessage("Please wait...")
            .setCancelable(false)
            .create()

        progressDialog.show()

        return progressDialog
    }

    fun Context.dismissProgressDialog(
        progressDialog: AlertDialog,
        title: String,
        message: String
    ) {
        progressDialog.dismiss()
        showAlert(message, title)
    }

}