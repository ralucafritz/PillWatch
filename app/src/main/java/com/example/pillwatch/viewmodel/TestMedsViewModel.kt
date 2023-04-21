package com.example.pillwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.MetadataEntity
import com.example.pillwatch.data.repository.MedsDataRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.utils.InteractionProperty
import com.example.pillwatch.utils.MedsDataProperty
import com.example.pillwatch.utils.MedsDataShaProperty
import kotlinx.coroutines.*
import timber.log.Timber

class TestMedsViewModel(
    medsDao: MedsDao,
    metadataDao: MetadataDao,
    application: Application
) :
    AndroidViewModel(application) {

    companion object {
        const val TAG_MEDS_DB = "MEDS_DATABASE"
        const val TAG_INTERACTION = "INTERACTION"
        const val SHA = "sha"
    }


    // repositories
    private val medsDataRepository: MedsDataRepository = MedsDataRepository(medsDao)
    private val metadataRepository: MetadataRepository = MetadataRepository(metadataDao)

    // mutable live data
    private val _responseMedsDataAPI = MutableLiveData<MedsDataShaProperty>()
    val responseMedsDataAPI: LiveData<MedsDataShaProperty>
        get() = _responseMedsDataAPI

    private val _responseInteractionDataAPI = MutableLiveData< List<InteractionProperty>>()
    val responseInteractionDataAPI: LiveData< List<InteractionProperty>>
        get() = _responseInteractionDataAPI

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
            _responseMedsDataAPI.value = responseAPICall

            // check if  SHA is new or it doesn't exist in the current database
            if (checkNewSha(_responseMedsDataAPI.value!!.sha)) {
                // transform the list from Property to Entity
                val entitiesList = _responseMedsDataAPI.value!!.file.map { transformDataToEntity(it) }
                //  check if the list is empty or not
                if (entitiesList.isNotEmpty()) {
                    // change to a different thread than Main
                    dbCoroutineScope.launch {
                        // Insert the meds in the database
                        medsDataRepository.insertAll(entitiesList)
                        // log the result
                        Timber.tag(TAG_MEDS_DB).d("Meds introduced to the database.")
                    }
                } else {
                    // log the result
                    Timber.tag(TAG_MEDS_DB).d("Failure: Meds were not introduced into the database. Duplicated CIM code detected.")
                }
            }
            // log the result
            Timber.tag(TAG_MEDS_DB).d("Meds were not introduced into the database. Data is the same, no need to update.")
        }
    }

    fun getInteractionDataFromAPI() {
        val interactionList = mutableListOf<InteractionProperty>()
        viewModelScope.launch {
            // call the API
                val responseInteractionDataAPI = AppApi.retrofitService.getInteractionData("207106", listOf("152923","656659"))

               for ( lists in responseInteractionDataAPI.interaction) {
                   lists.forEach {
                       interactionList.add(it)
                   }
               }
                _responseInteractionDataAPI.value = interactionList
                Timber.tag(TAG_INTERACTION).d(responseInteractionDataAPI.toString())
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
            Timber.tag(TAG_MEDS_DB).d("SHA value inserted")
            return true
        } else if (currentSha.metadataValue != newSha) {
            // change from Main thread
            dbCoroutineScope.launch {
                // update the current sha value with the new value
                metadataRepository.update(MetadataEntity(currentSha.id, SHA, newSha))
                // clear the meds_data_table in order for the new data to be introduced
                clearMedsData()
            }
            // log the result
            Timber.tag(TAG_MEDS_DB).d("SHA value updated")
            return true
        }
        // log the result
        Timber.tag(TAG_MEDS_DB).d("SHA value is the same")
        return false
    }

    private fun clearMedsData() {
        // change from Main Thread
        dbCoroutineScope.launch {
            // clear the meds_data_table
            medsDataRepository.clear()
            // log the result
            Timber.tag(TAG_MEDS_DB).d("Meds table cleared successfully.")
        }
    }

    fun clearMetadata() {
        // change from Main thread
        dbCoroutineScope.launch {
            // clear the metadata_table
            metadataRepository.clear()
            // log the result
            Timber.tag(TAG_MEDS_DB).d("Metadata table cleared successfully.")
        }
    }

    private fun transformDataToEntity(data: MedsDataProperty): MedsEntity {
        // return the entity from the property
        return MedsEntity(
            0L,
            data.tradeName,
            data.dci,
            data.dosageForm,
            data.concentration,
            data.atcCode,
            data.lastUpdateDate,
            data.rxCui
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}
