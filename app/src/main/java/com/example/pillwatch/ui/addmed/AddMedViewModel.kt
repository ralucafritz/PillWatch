package com.example.pillwatch.ui.addmed

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.network.AppApi
import com.example.pillwatch.user.UserManager
import com.example.pillwatch.utils.InteractionProperty
import com.example.pillwatch.utils.extensions.ContextExtensions.showAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@LoggedUserScope
class AddMedViewModel @Inject constructor(
    private val medsRepository: MedsRepository,
    private val userMedsRepository: UserMedsRepository,
    private val userManager: UserManager
) : ViewModel() {


    val medName = MutableLiveData("")
    val concentrationEditText = MutableLiveData("")

    private val _responseInteractionDataAPI = MutableLiveData<List<InteractionProperty>>()
    private val _selectedMedNumber = MutableLiveData<Int>()
    private val _selectedMed = MutableLiveData<MedsEntity>()
    private val _pairList = MutableLiveData<List<MedsEntity>>()

    val concentrationList: MutableList<String> = mutableListOf()
    val nameList: MutableList<String> = mutableListOf()

    val addMedCheck = MutableLiveData<Boolean?>()

    private val severityHigh: MutableList<Pair<String, String>> = mutableListOf()
    private val severityModerate: MutableList<Pair<String, String>> = mutableListOf()
    private val severityLow: MutableList<Pair<String, String>> = mutableListOf()

    private val _medAddedId = MutableLiveData<Long?>()
    val medAddedId: LiveData<Long?>
        get() = _medAddedId

    private val _navigationCheck = MutableLiveData<Boolean?>()
    val navigationCheck: LiveData<Boolean?>
        get() = _navigationCheck

    private val _isAlertNeeded = MutableLiveData<Boolean>()
    val isAlertNeeded: LiveData<Boolean>
        get() = _isAlertNeeded

    fun searchMedName(medName: String) {
        viewModelScope.launch {
            val medsSearch = withContext(Dispatchers.IO) {
                medsRepository.searchMedsWithName(medName)
            }
            if (medsSearch.isNotEmpty()) {
                _pairList.value = medsSearch
                val stringList = mutableListOf<String>()
                val concList = mutableListOf<String>()
                _pairList.value!!.forEach {
                    stringList.add(it.tradeName)
                    concList.add(it.concentration)
                    nameList.clear()
                    concentrationList.clear()
                    nameList.addAll(stringList)
                    concentrationList.addAll(concList)
                }
            } else {
                nameList.addAll(emptyList())
                concentrationList.addAll(emptyList())
            }
        }
    }

    fun getPairAtPosition(position: Int) {
        _selectedMedNumber.value = position
        _selectedMed.value = _pairList.value?.get(position)
        concentrationEditText.value = _selectedMed.value!!.concentration
    }

    suspend fun addMedToUser() {
        val userId = userManager.id
        val medId: Long?
        val name: String
        if (_selectedMed.value != null && medName.value != "") {
            if (_selectedMed.value!!.tradeName == medName.value) {
                medId = _selectedMed.value!!.id
                name = _selectedMed.value!!.tradeName
            } else {
                medId = null
                name = medName.value!!
            }
            if (medId != null) {
                viewModelScope.launch {
                    checkInteraction(medId, userId)
                    waitUntilAddMedCheck()
                    if (addMedCheck.value != null && addMedCheck.value == true) {
                        _medAddedId.value = withContext(Dispatchers.IO) {
                            userMedsRepository.insert(
                                UserMedsEntity(
                                    0L,
                                    name,
                                    userId,
                                    medId,
                                    concentrationEditText.value
                                )
                            )
                        }
                        if (_medAddedId.value != null) {
                            _navigationCheck.value = true
                        }
                    } else {
                        addMedCheck.value = null
                    }
                }
            }
        } else {
            if (medName.value != "") {
                _medAddedId.value = withContext(Dispatchers.IO) {
                    userMedsRepository.insert(
                        UserMedsEntity(
                            0L,
                            medName.value!!,
                            userId,
                            null,
                            concentrationEditText.value
                        )
                    )
                }
                if (_medAddedId.value != null) {
                    _navigationCheck.value = true
                }
            }
        }
    }

    private suspend fun waitUntilAddMedCheck(): Boolean = suspendCoroutine { continuation ->
        val observer = object : Observer<Boolean?> {
            override fun onChanged(value: Boolean?) {
                if (value != null) {
                    addMedCheck.removeObserver(this)
                    continuation.resume(value)
                }
            }
        }
        addMedCheck.observeForever(observer)
    }

    private suspend fun getRxCuiList(list: List<Long>): List<String> {
        val rxCuiList = mutableListOf<String>()
        list.map {
            val rxCui = withContext(Dispatchers.IO) { medsRepository.getRxCuiForMed(it) }
            rxCuiList.add(rxCui)
        }
        return rxCuiList
    }

    private fun checkInteraction(medId: Long, userId: Long) {
        viewModelScope.launch {
            // get rxcui based on the medId from Db
            val medIdRxCui = withContext(Dispatchers.IO) {
                medsRepository.getRxCuiForMed(medId)
            }
            // get rxcui's fror the rest of the meds in the user's list -> return only the values different than null
            val rxCuiList = withContext(Dispatchers.IO) {
                val medIds = userMedsRepository.getMedIdForMedsForUser(userId).filterNotNull()
                getRxCuiList(medIds)
            }
            if (rxCuiList.isNotEmpty()) {
                getInteractionDataFromAPI(medIdRxCui, rxCuiList)
            } else {
                addMedCheck.value = true
            }
        }
    }

    private fun getInteractionDataFromAPI(
        rxCui: String,
        listRxCui: List<String>
    ) {
        val interactionList = mutableListOf<InteractionProperty>()
        viewModelScope.launch {
            try {
                severityModerate.clear()
                severityHigh.clear()
                severityLow.clear()
                // call the API
                val responseInteractionDataAPI =
                    AppApi.retrofitService.getInteractionData(rxCui, listRxCui)

                for (lists in responseInteractionDataAPI.interaction) {
                    lists.forEach {
                        when (it.severity.lowercase()) {
                            "low" -> {
                                severityLow.add(Pair(it.rxCui1, it.rxCui2))
                            }

                            "medium", "moderate" -> {
                                severityModerate.add(Pair(it.rxCui1, it.rxCui2))
                            }

                            "high" -> {
                                severityHigh.add(Pair(it.rxCui1, it.rxCui2))
                            }

                            else -> {
                                //
                            }
                        }
                        interactionList.add(it)
                    }
                }
                if (interactionList.isNotEmpty()) {
                    _responseInteractionDataAPI.value = interactionList
                }
                _isAlertNeeded.value = true
            } catch (e: Exception) {
                Timber.tag("INTERACTION").e("Server error $e.printStackTrace().toString()")
            }
        }
    }

    fun isAlertNeeded(context: Context) {
        val highCount = severityHigh.size
        val moderateCount = severityModerate.size
        val lowCount = severityLow.size
        val messagePair = checkSeverity(lowCount, moderateCount, highCount)
        if (messagePair.second > 0) {
            context.showAlert(
                messagePair.first,
                "INTERACTION ALERT!",
                "Yes",
                "No"
            ) { positiveButtonPressed -> addMedCheck.value = positiveButtonPressed }
        } else {
            addMedCheck.value = true
        }
        _isAlertNeeded.value = false
        Timber.tag("INTERACTION").d(addMedCheck.value.toString())
    }

    private fun checkSeverity(
        lowCount: Int,
        moderateCount: Int,
        highCount: Int
    ): Pair<String, Int> {
        val builder =
            StringBuilder("The following interaction/s have been detected between this medicine and the other medicine on your list: \n\n")
        var count = 0
        if (lowCount > 0) {
            val severityLowPairs =
                severityLow.filter { it.first == _selectedMed.value?.rxCui || it.second == _selectedMed.value?.rxCui }
            builder.append("- ${severityLowPairs.size} low severity\n")
            count += severityLowPairs.size
        }
        if (moderateCount > 0) {
            val severityModeratePairs =
                severityModerate.filter { it.first == _selectedMed.value?.rxCui || it.second == _selectedMed.value?.rxCui }
            builder.append("- ${severityModeratePairs.size} moderate severity\n")
            count += severityModeratePairs.size
        }
        if (highCount > 0) {
            val severityHighPairs =
                severityHigh.filter { it.first == _selectedMed.value?.rxCui || it.second == _selectedMed.value?.rxCui }
            builder.append("- ${severityHighPairs.size} high severity\n")
            count += severityHighPairs.size
        }
        builder.append("\n\n Do you still wish to proceed?")

        return Pair(builder.toString(), count)
    }

    fun navigationCompleteToAlarmFrequency() {
        _navigationCheck.value = null
        _medAddedId.value = null
    }

}