package com.example.pillwatch.testmeds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pillwatch.database.dao.MedsDataDao
import com.example.pillwatch.database.dao.MetadataDao
import com.example.pillwatch.database.entity.MedsDataEntity
import com.example.pillwatch.database.entity.MetadataEntity
import com.example.pillwatch.database.repository.MedsDataRepository
import com.example.pillwatch.database.repository.MetadataRepository
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.network.MedsDataProperty
import com.example.pillwatch.network.MedsDataShaProperty
import kotlinx.coroutines.*
import timber.log.Timber

class TestMedsViewModel(
    val medsDataDao: MedsDataDao,
    metadataDao: MetadataDao,
    application: Application
) :
    AndroidViewModel(application) {

    private val SHA = "sha"
    private val medsDataRepository: MedsDataRepository = MedsDataRepository(medsDataDao)
    private val metadataRepository: MetadataRepository = MetadataRepository(metadataDao)

    // mutable live data
    private val _responseAPI = MutableLiveData<MedsDataShaProperty>()
    val responseAPI: LiveData<MedsDataShaProperty>
        get() = _responseAPI

    // coroutines stuff
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun getMedsDataFromAPI() {
        coroutineScope.launch {
            val responseAPICall = AppApi.retrofitService.getDataset()
            _responseAPI.value = responseAPICall
            if (checkNewSha(_responseAPI.value!!.sha)) {
                val entitiesList = _responseAPI.value!!.file.map { it -> transformDataToEntity(it) }
                if (entitiesList.isNotEmpty()) {
                    val cimCode = withContext(Dispatchers.IO) {
                        medsDataRepository.getFirstCIM()
                    }
                    insertMeds(entitiesList, cimCode)
                }
            }
        }
    }

    private suspend fun checkNewSha(newSha: String): Boolean {
        val currentSha = withContext(Dispatchers.IO) {
            metadataRepository.getMetadata(SHA)
        }
        if (currentSha == null) {
            dbCoroutineScope.launch {
                metadataRepository.insert(
                    MetadataEntity(
                        0L,
                        SHA,
                        newSha
                    )
                )
            }
            Timber.d("SHA value inserted")
            return true
        } else if (currentSha.value != newSha) {
            dbCoroutineScope.launch {
                metadataRepository.update(MetadataEntity(currentSha.id, SHA, newSha))
            }
            Timber.d("SHA value updated")
            return true
        }
        Timber.d("SHA value is the same")
        return false
    }

    private suspend fun insertMeds(entitiesList: List<MedsDataEntity>, cimCode: String?) {
        if (cimCode == null || entitiesList[0].cimCode != cimCode) {
            dbCoroutineScope.launch {
                medsDataRepository.insertAll(entitiesList)
                Timber.d("Meds introduced to the database.")
            }
        } else {
            Timber.d("Failure: Meds were not introduced to the database. Duplicated CIM code detected.")
        }
    }

    fun clearMedsData() {
        dbCoroutineScope.launch {
            medsDataRepository.clear()
            Timber.d("Meds table cleared successfully.")

        }
    }

    fun clearMetadata() {
        dbCoroutineScope.launch {
            metadataRepository.clear()
            Timber.d("Metadata table cleared successfully.")

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
