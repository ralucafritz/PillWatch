package com.example.pillwatch.fragments.testmeds

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

    // repositories
    private val medsDataRepository: MedsDataRepository = MedsDataRepository(medsDataDao)
    private val metadataRepository: MetadataRepository = MetadataRepository(metadataDao)

    // mutable live data
    private val _responseAPI = MutableLiveData<MedsDataShaProperty>()
    val responseAPI: LiveData<MedsDataShaProperty>
        get() = _responseAPI

    // coroutines stuff

    // start the job
    private var viewModelJob = Job()
    // coroutine scope for main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    // coroutine scope for database stuff
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun getMedsDataFromAPI() {
        coroutineScope.launch {
            // call the API
            val responseAPICall = AppApi.retrofitService.getDataset()
            _responseAPI.value = responseAPICall

            // check if  SHA is new or it doesn't exist in the current database
            if (checkNewSha(_responseAPI.value!!.sha)) {
                // transform the list from Property to Entity
                val entitiesList = _responseAPI.value!!.file.map { it -> transformDataToEntity(it) }
                //  check if the list is empty or not
                if (entitiesList.isNotEmpty()) {
                    // change to a different thread than Main
                    dbCoroutineScope.launch {
                        // Insert the meds in the database
                        medsDataRepository.insertAll(entitiesList)
                        // log the result
                        Timber.tag("database").d("Meds introduced to the database.")
                    }
                } else {
                    // log the result
                    Timber.tag("database").d("Failure: Meds were not introduced into the database. Duplicated CIM code detected.")
                }
            }
            // log the result
            Timber.tag("database").d("Meds were not introduced into the database. Data is the same, no need to update.")
        }
    }

    private suspend fun checkNewSha(newSha: String): Boolean {
        // get the current value of SHA
        val currentSha = withContext(Dispatchers.IO) {
            metadataRepository.getMetadata(SHA)
        }
        // check if the current value is null -> no current sha detected
        if (currentSha == null) {
            // change from Main thread
            dbCoroutineScope.launch {
                // insert the new sha
                metadataRepository.insert(
                    MetadataEntity(
                        0L,
                        SHA,
                        newSha
                    )
                )
            }
            // log the result
            Timber.tag("database").d("SHA value inserted")
            return true
        } else if (currentSha.value != newSha) {
            // change from Main thread
            dbCoroutineScope.launch {
                // update the current sha value with the new value
                metadataRepository.update(MetadataEntity(currentSha.id, SHA, newSha))
                // clear the meds_data_table in order for the new data to be introduced
                clearMedsData()
            }
            // log the result
            Timber.tag("database").d("SHA value updated")
            return true
        }
        // log the result
        Timber.tag("database").d("SHA value is the same")
        return false
    }

    fun clearMedsData() {
        // change from Main Thread
        dbCoroutineScope.launch {
            // clear the meds_data_table
            medsDataRepository.clear()
            // log the result
            Timber.tag("database").d("Meds table cleared successfully.")
        }
    }

    fun clearMetadata() {
        // change from Main thread
        dbCoroutineScope.launch {
            // clear the metadata_table
            metadataRepository.clear()
            // log the result
            Timber.tag("database").d("Metadata table cleared successfully.")
        }
    }

    private fun transformDataToEntity(data: MedsDataProperty): MedsDataEntity {
        // return the entity from the property
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
