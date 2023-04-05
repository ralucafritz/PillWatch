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
import kotlinx.coroutines.*
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

    private val _cimCode = MutableLiveData<String?>()
    val cimCode: LiveData<String?>
        get() = _cimCode

    // coroutines stuff
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun getMedsDataFromAPI() {
        coroutineScope.launch {
            val medsDataApiCall = AppApi.retrofitService.getDataset()
            _shaProperty.value = medsDataApiCall
            val entitiesList = _shaProperty.value!!.file.map { it -> transformDataToEntity(it) }
            if (!entitiesList.isEmpty()) {
                val cimCode = withContext(Dispatchers.IO) { databaseDao.getFirstCIM() }
//                Timber.d("${_cimCode.value}, {$entitiesList[0].cimCode}")

                if(cimCode == null || entitiesList[0].cimCode != cimCode) {
                    dbCoroutineScope.launch {
                        medsDataRepository.insertAll(entitiesList)
                        Timber.d("Meds introduced to the database.")}
                }
                else {
                    Timber.d("Failure: Meds were not introduced to the database. Duplicated CIM code detected.")
                }
            }
        }
    }

    fun clearData() {
        dbCoroutineScope.launch{ databaseDao.clear()
            Timber.d("Meds database clear successful.")

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
