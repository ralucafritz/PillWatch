package com.example.pillwatch.utils.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.pillwatch.R
import com.google.android.material.snackbar.Snackbar

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
        val marginVertical = (100 * resources.displayMetrics.density).toInt()
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, marginVertical)
        toast.show()
    }

    fun View.snackbar(msg:String, colorAttr: Int = -1, duration:  Long = 5000L, marginBottom: Int = 50) {
        val snackbar = Snackbar.make(this, msg, Snackbar.LENGTH_INDEFINITE)

        // Set margins
        val layoutParams = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = (marginBottom * resources.displayMetrics.density).toInt()
        snackbar.view.layoutParams = layoutParams


        // Set text color and alignment
        val snackbarTextView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        snackbarTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        if(colorAttr != -1) {
            // Set background tint color
            val colorMissedValue = context.getThemeColor(colorAttr)
            ViewCompat.setBackgroundTintList(
                snackbar.view,
                ColorStateList.valueOf(colorMissedValue)
            )
            snackbarTextView.setTextColor(ContextCompat.getColor(context, android.R.color.background_light))
        }

        // Make the text bold
        snackbarTextView.setTypeface(snackbarTextView.typeface, Typeface.BOLD)

        // Set max lines to avoid truncation
        snackbarTextView.maxLines = Int.MAX_VALUE

        snackbar.show()

        snackbar.view.setOnClickListener {
            snackbar.dismiss()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            snackbar.dismiss()
        }, duration)  // Dismiss after 10 seconds
    }

    fun Context.getThemeColor(
        @AttrRes attrResId: Int
    ): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
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
        val builder = AlertDialog.Builder(
            this,
            R.style.RoundedDialogStyle
        )
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
        val progressDialog = AlertDialog.Builder(this, R.style.RoundedDialogStyle)
            .setView(LayoutInflater.from(this).inflate(R.layout.dialog_progress, null))
            .setTitle(title)
            .setCancelable(false)
            .create()

        progressDialog.show()

        return progressDialog
    }

    fun Context.dismissProgressDialog(
        progressDialog: AlertDialog,
        title: String="",
        message: String=""
    ) {
        progressDialog.dismiss()
        if(title != "") {
            showAlert(message, title)
        }
    }
}