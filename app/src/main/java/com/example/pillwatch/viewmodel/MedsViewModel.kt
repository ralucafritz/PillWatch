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
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.utils.InteractionProperty
import com.example.pillwatch.utils.MedsDataProperty
import com.example.pillwatch.utils.MedsDataShaProperty
import kotlinx.coroutines.*
import timber.log.Timber

class MedsViewModel(
    medsDao: MedsDao,
    metadataDao: MetadataDao,
    application: Application
) :
    AndroidViewModel(application) {

    companion object {
        const val TAG_MEDS_DB = "MEDS_DATABASE"
        const val TAG_INTERACTION = "INTERACTION"
        const val SHA = "sha"
        const val UPDATE_SUCCESS = "Update successful."
        const val UPDATE_FAILED = "Update failed."
        const val UP_TO_DATE = "Medication data is up to date."
        const val SERVER_ERR = "Server is unavailable."
    }

    // repositories
    private val medsRepository: MedsRepository = MedsRepository(medsDao)
    private val metadataRepository: MetadataRepository = MetadataRepository(metadataDao)
    // mutable live data
    private val _responseMedsDataAPI = MutableLiveData<MedsDataShaProperty>()
    private val _checkUpdateResult = MutableLiveData<Boolean?>()
    private val _updateMessage = MutableLiveData<String>()
    val updateMessage: LiveData<String>
        get() = _updateMessage

    private val _updateDialogTitle = MutableLiveData<String>()
    val updateDialogTitle: LiveData<String>
        get() = _updateDialogTitle

    // coroutines stuff

    // start the job
    private var viewModelJob = Job()

    // coroutine scope for main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // coroutine scope for database stuff
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    suspend fun getMedsDataFromAPI() : Result<Boolean> {
        val result = withContext(Dispatchers.Main + viewModelJob){
            try {
                // call the API
                val responseAPICall = AppApi.retrofitService.getDataset()
                _responseMedsDataAPI.value = responseAPICall

                // check if  SHA is new or it doesn't exist in the current database
                if (checkNewSha(_responseMedsDataAPI.value!!.sha)) {
                    // transform the list from Property to Entity
                    val entitiesList =
                        _responseMedsDataAPI.value!!.file.map { transformDataToEntity(it) }
                    //  check if the list is empty or not
                    if (entitiesList.isNotEmpty()) {
                        // change to a different thread than Main
                        dbCoroutineScope.launch {
                            medsRepository.clear()
                            // Insert the meds in the database
                            medsRepository.insertAll(entitiesList)
                        }
                        // log the result
                        _updateDialogTitle.value = "Success"
                        _updateMessage.value = UPDATE_SUCCESS
                        Timber.tag(TAG_MEDS_DB).d("Meds introduced to the database. $UPDATE_SUCCESS")
                        true
                    } else {
                        _updateDialogTitle.value ="Failure"
                        _updateMessage.value = UPDATE_FAILED
                        // log the result
                        Timber.tag(TAG_MEDS_DB)
                            .d("Failure: Meds were not introduced into the database. $UPDATE_FAILED")
                        false
                    }
                } else {
                    // log the result
                    _updateDialogTitle.value = "No update required"
                    _updateMessage.value = UP_TO_DATE
                    Timber.tag(TAG_MEDS_DB)
                        .d("Meds were not introduced into the database. $UP_TO_DATE")
                    false
                }
            } catch (e: Exception) {
                _updateDialogTitle.value = "Error"
                _updateMessage.value = SERVER_ERR
                Timber.tag(TAG_MEDS_DB).e("$UPDATE_FAILED $SERVER_ERR $e.printStackTrace().toString()")
                false
            }
        }
        _checkUpdateResult.postValue(result)
        return Result.success(result)
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
            }
            // log the result
            Timber.tag(TAG_MEDS_DB).d("SHA value updated")
            return true
        }
        // log the result
        Timber.tag(TAG_MEDS_DB).d("SHA value is the same")
        return false
    }

     fun clearMedsData() {
        // change from Main Thread
        dbCoroutineScope.launch {
            // clear the meds_data_table
            medsRepository.clear()
            // log the result
            Timber.tag(TAG_MEDS_DB).d("Meds table cleared successfully.")
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
