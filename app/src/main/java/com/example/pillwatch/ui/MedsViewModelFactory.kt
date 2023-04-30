package com.example.pillwatch.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.source.local.MedsDao
import com.example.pillwatch.data.source.local.MetadataDao

class MedsViewModelFactory(
    private val medsDao: MedsDao,
    private val metadataDao: MetadataDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedsViewModel::class.java)) {
            return MedsViewModel(medsDao, metadataDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}