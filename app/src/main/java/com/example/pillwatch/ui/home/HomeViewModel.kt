package com.example.pillwatch.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

@LoggedUserScope
class HomeViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val alarmRepository: AlarmRepository,
    private val userManager: UserManager
) : ViewModel() {


    private val _userMedsList = MutableLiveData<MutableList<Pair<UserMedsEntity, Long>>?>(mutableListOf())


    /**
     * Retrieves the list of user medications along with the next alarm time for each medication.
     *
     * @return The list of user medications along with the next alarm time.
     */
    suspend fun getMedsList(): MutableList<Pair<UserMedsEntity, Long>>? {
        val medsList = withContext(Dispatchers.IO) {
            val userId = userManager.id
            val filteredList: MutableList<Pair<UserMedsEntity, Long>> = mutableListOf()
            val list = if (userId != -1L) {
                userMedsRepository.getAllMedsForUser(userId)
            } else {
                null
            }
            if (list != null) {
                list.forEach{ med ->
                    val currentTime = System.currentTimeMillis()
                    val midnightInMillis = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis
                    val alarm = alarmRepository.getNextAlarmBeforeMidnight(med.id, currentTime, midnightInMillis)
                    if(alarm != null) {
                        filteredList.add(Pair(med, alarm.timeInMillis))
                    }
                }
                filteredList
            } else {
                null
            }
        }

       _userMedsList.value = medsList

    return medsList
}

}