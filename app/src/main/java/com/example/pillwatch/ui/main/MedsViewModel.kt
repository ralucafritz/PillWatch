package com.example.pillwatch.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.MetadataEntity
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.utils.MedsDataProperty
import com.example.pillwatch.utils.MedsDataShaProperty
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class MedsViewModel @Inject constructor(
    private val medsRepository: MedsRepository,
    private val metadataRepository: MetadataRepository,
) :
    ViewModel() {

    companion object {
        const val TAG_MEDS_DB = "MEDS_DATABASE"
        const val SHA = "sha"
        const val UPDATE_SUCCESS = "Update successful."
        const val UPDATE_FAILED = "Update failed."
        const val UP_TO_DATE = "Medication data is up to date."
        const val SERVER_ERR = "Server is unavailable."
    }

    // mutable live data
    private val _responseMedsDataAPI = MutableLiveData<MedsDataShaProperty>()
    private val _checkUpdateResult = MutableLiveData<Boolean?>()
    private val _updateMessage = MutableLiveData<String>()
    val updateMessage: LiveData<String>
        get() = _updateMessage

    private val _updateDialogTitle = MutableLiveData<String>()
    val updateDialogTitle: LiveData<String>
        get() = _updateDialogTitle

    suspend fun getMedsDataFromAPI(): Result<Boolean> {
        val result = withContext(Dispatchers.Main) {
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
                        withContext(Dispatchers.IO) {
                            medsRepository.clear()
                            // Insert the meds in the database
                            medsRepository.insertAll(entitiesList)
                        }
                        // log the result
                        logMeds("Success", UPDATE_SUCCESS)
                        true
                    } else {
                        // log the result
                        logMeds("Failure", UPDATE_FAILED)
                        false
                    }
                } else {
                    // log the result
                    logMeds("No update required", UP_TO_DATE)
                    false
                }
            } catch (e: Exception) {
                logMeds("Error", SERVER_ERR, e.printStackTrace().toString())
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
            withContext(Dispatchers.IO) {
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
            logSha(true)
            return true
        } else if (currentSha.metadataValue != newSha) {
            // change from Main thread
            withContext(Dispatchers.IO) {
                // update the current sha value with the new value
                metadataRepository.update(MetadataEntity(currentSha.id, SHA, newSha))
                // clear the meds_data_table in order for the new data to be introduced
            }
            // log the result
            logSha(null)
            return true
        }
        // log the result
        logSha(false)
        return false
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

    private fun logMeds(title: String, status: String, errMsg: String = "") {
        _updateDialogTitle.value = title
        _updateMessage.value = status

        val logMessage = when (status) {
            UPDATE_SUCCESS -> "Meds introduced to the database. $UPDATE_SUCCESS"
            UPDATE_FAILED -> "Failure: Meds were not introduced into the database. $UPDATE_FAILED"
            UP_TO_DATE -> "Meds were not introduced into the database. $UP_TO_DATE"
            SERVER_ERR -> "$UPDATE_FAILED $SERVER_ERR $errMsg"
            else -> ""
        }
        Timber.tag(TAG_MEDS_DB).d(logMessage)
    }

    private fun logSha(value: Boolean?) {
        val message = when (value) {
            false -> "SHA value is the same"
            null -> "SHA value updated"
            true -> "SHA value inserted"
        }
        Timber.tag(TAG_MEDS_DB).d(message)
    }

}
