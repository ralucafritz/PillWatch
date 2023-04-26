package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.UserMedsDao
import com.example.pillwatch.viewmodel.MedicationViewModel



class MedicationViewModelFactory(
    private val userMedsDao: UserMedsDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            return MedicationViewModel(userMedsDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}