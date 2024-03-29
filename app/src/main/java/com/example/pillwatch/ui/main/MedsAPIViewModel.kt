package com.example.pillwatch.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

/**
 * ViewModel class for the Medication screen.
 *
 * @param medsRepository The repository for medication-related database operations.
 * @param metadataRepository The repository for metadata-related database operations.
 */
class MedsAPIViewModel @Inject constructor(
    private val medsRepository: MedsRepository,
    private val metadataRepository: MetadataRepository,
) :
    ViewModel() {

    companion object {
        const val TAG_MEDS_DB = "MEDS_DATABASE"
        const val SHA = "sha"
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

    /**
     * Retrieves medication data from the API and updates the local database if necessary.
     * Returns the result of the update operation.
     */
    suspend fun getMedsDataFromAPI(
        checkCount : Boolean = true,
        arrayMessages: Array<String>,
        arrayTitles: Array<String>
    ): Result<Boolean> {
        val result = withContext(Dispatchers.Main) {
            if(checkCount) {
                val count = withContext(Dispatchers.IO) {
                    medsRepository.getMedsCount()
                }
                if (count != 0) {
                    return@withContext false
                }
            }
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
                        logMeds(arrayTitles[0], arrayMessages[0], 0)
                        true
                    } else {
                        // log the result
                        logMeds(arrayTitles[1], arrayMessages[1], 1)
                        false
                    }
                } else {
                    // log the result
                    logMeds(arrayTitles[2], arrayMessages[2], 2)
                    false
                }
            } catch (e: Exception) {
                logMeds(arrayTitles[3], arrayMessages[3] ,3, e.printStackTrace().toString())
                false
            }
        }
        _checkUpdateResult.postValue(result)
        return Result.success(result)
    }

    /**
     * Checks if the retrieved SHA value is new or different from the current SHA value stored in the metadata.
     * Updates the SHA value in the metadata and triggers the update operation if necessary.
     */
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

    /**
     * Transforms the medication data from the property format to the entity format.
     *
     * @param data The medication data in property format.
     * @return The medication data in entity format.
     */
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

    /**
     * Logs the medication update operation result.
     *
     * @param title The title of the update dialog.
     * @param status The status message of the update operation.
     * @param errMsg The error message, if any.
     */
    private fun logMeds(title: String, status: String, pos: Int, errMsg: String = "") {
        _updateDialogTitle.value = title
        _updateMessage.value = status

        val logMessage = when (pos) {
            0 -> "Meds introduced to the database. $status"
            1 -> "Failure: Meds were not introduced into the database. $status"
            2 -> "Meds were not introduced into the database. $status"
            3 -> "$status $errMsg"
            else -> ""
        }
        Timber.tag(TAG_MEDS_DB).d(logMessage)
    }

    /**
     * Logs the SHA value update result.
     *
     * @param value The result of the SHA update operation.
     */
    private fun logSha(value: Boolean?) {
        val message = when (value) {
            false -> "SHA value is the same"
            null -> "SHA value updated"
            true -> "SHA value inserted"
        }
        Timber.tag(TAG_MEDS_DB).d(message)
    }

}
