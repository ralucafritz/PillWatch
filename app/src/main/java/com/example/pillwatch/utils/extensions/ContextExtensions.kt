package com.example.pillwatch.utils.extensions

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pillwatch.R

object ContextExtensions {
    /**
     *
     *      Extension function to show a toast message
     *      @param msg is the message that is about to be showed
     *
     */
    fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Context.toastTop(msg: String) {
        val toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()

    }


    /**
     *      Extension function to set the value of the `isLoggedIn` preference from SharedPreferences
     */
    fun Context.setLoggedInStatus(value: Boolean) {
        SharedPreferencesExtensions.setPreference(this, "isLoggedIn", value)
    }

    /**
     *      Extension function to get the value of the 'isLoggedIn` preference from SharedPreferences
     */
    fun Context.getLoggedInStatus(): Boolean {
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
    fun Context.getPreference(key: String, type: Any): Any {
        return SharedPreferencesExtensions.getPreference(this, key, type)
    }

    /**
     *      Extension function to get a string preference value from SharedPreferences
     */
    fun Context.getPreference(key: String): String {
        return SharedPreferencesExtensions.getPreference(this, key, "")
    }

    /**
     *      Extension function to check if the device is currently connected to the internet
     */
    fun Context.isInternetConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    /**
     *      Extension function to show a simple alert dialog
     */
    fun Context.showAlert(
        message: String,
        title: String = "",
        positiveButtonText: String = "OK",
        negativeButtonText: String? = null,
        callback: ((positiveButtonPressed: Boolean) -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(this, com.google.android.material.R.style.Base_Theme_Material3_Dark_Dialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                callback?.invoke(true)
            }

        negativeButtonText?.let {
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                callback?.invoke(false)
            }
        }

        builder.create().show()
    }

    fun Context.showProgressDialog(
        title: String,
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

    private fun Context.isDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }


}