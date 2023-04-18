package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.viewmodel.LoadingViewModel


class LoadingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
            return LoadingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}