package com.example.pillwatch.ui.medication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@LoggedUserScope
class MedicationViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val userManager: UserManager,
    private val medsLogRepository: MedsLogRepository
) :
    ViewModel() {
    // LiveData to hold the list of user medications
    private val _userMedsList = MutableLiveData<List<UserMedsEntity>>(listOf())

    private val _logs = MutableLiveData<List<List<Long>>>()
    val logs: LiveData<List<List<Long>>>
        get() = _logs

    /**
     * Retrieves the list of user medications from the repository.
     *
     * @return The list of user medications.
     */
    suspend fun getMedsList(): List<UserMedsEntity> {
        val medsList = withContext(Dispatchers.IO) {
            val userId = userManager.id
             if (userId != -1L) {
                userMedsRepository.getAllMedsForUser(userId)
            } else {
                null
            }
        }
        _userMedsList.value = medsList!!
        return medsList
    }

    /**
     * Generates the share text for the user's medication history.
     *
     * @return The share text for the medication history.
     */
    fun getMedsShareText(): String {
        val medsList = _userMedsList.value ?: return ""

        val now = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(now.time)

        val shareText = buildString {
            append("Hey, here's my medicine history from PillWatch as of today, $formattedDate! \n\n")
            medsList.forEachIndexed { index, med ->
                append("No. ${index + 1} ${med.tradeName}")
                med.concentration?.let { concentration ->
                    if (concentration.isNotBlank()) {
                        append(" $concentration")
                    }
                }
                appendLine()
            }
        }

        return shareText
    }

    /**
     * Retrieves the logs for the user's medications.
     */
    fun getLogs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val medsList = _userMedsList.value ?: return@withContext
                val logsList = mutableListOf<List<Long>>()

                medsList.forEach { med ->
                    val takenCount =
                        medsLogRepository.getLogCountByStatus(med.id, TakenStatus.TAKEN)
                    val postponedCount =
                        medsLogRepository.getLogCountByStatus(med.id, TakenStatus.POSTPONED)
                    val missedCount =
                        medsLogRepository.getLogCountByStatus(med.id, TakenStatus.MISSED)

                    val pairList = listOf(
                        takenCount,
                        postponedCount,
                        missedCount
                    )

                    logsList.add(pairList)
                }

                _logs.postValue(logsList)
            }
        }
    }
}