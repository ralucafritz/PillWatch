package com.example.pillwatch.testmeds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pillwatch.database.dao.DatabaseDao
import com.example.pillwatch.database.entity.MedsDataEntity
import com.example.pillwatch.database.repository.MedsDataRepository
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.network.MedsDataProperty
import com.example.pillwatch.network.MedsDataShaProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class TestMedsViewModel(val databaseDao: DatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private val medsDataRepository: MedsDataRepository = MedsDataRepository(databaseDao)

    // mutable live data
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    var file = emptyList<MedsDataProperty>();

    private val _shaProperty = MutableLiveData<MedsDataShaProperty>()
    val shaProperty: LiveData<MedsDataShaProperty>
        get() = _shaProperty

    // coroutines stuff
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun getMedsData() {
        coroutineScope.launch {
            val medsDataApiCall = AppApi.retrofitService.getDataset()
            Timber.d("test")
            _shaProperty.value = medsDataApiCall
            val entitiesList = _shaProperty.value!!.file.map { it -> transformDataToEntity(it) }
            if (!entitiesList.isEmpty()) {
                dbCoroutineScope.launch { medsDataRepository.insertAll(entitiesList) }
            }
        }
    }

    private fun transformDataToEntity(data: MedsDataProperty): MedsDataEntity {
        Timber.d("in transformDataToEntity")
        return MedsDataEntity(
            0L,
            data.cimCode,
            data.tradeName,
            data.dci,
            data.dosageForm,
            data.concentration,
            data.atcCode,
            data.prescriptionType,
            data.packageVolume,
            data.lastUpdateDate,
            data.rxCui
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
