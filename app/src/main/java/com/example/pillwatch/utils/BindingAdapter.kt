package com.example.pillwatch.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("liveDataToString")
fun liveDataToString(textView: TextView, liveData: LiveData<Any>?) {
    textView.text = liveData?.value?.toString() ?: ""
}