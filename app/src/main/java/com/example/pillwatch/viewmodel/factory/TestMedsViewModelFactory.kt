package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.MedsDataDao
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.viewmodel.TestMedsViewModel

class TestMedsViewModelFactory(
    private val medsDataDao: MedsDataDao,
    private val metadataDao: MetadataDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestMedsViewModel::class.java)) {
            return TestMedsViewModel(medsDataDao, metadataDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}