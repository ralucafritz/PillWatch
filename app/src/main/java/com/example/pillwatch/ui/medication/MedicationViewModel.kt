package com.example.pillwatch.ui.medication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@LoggedUserScope
class MedicationViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val userManager: UserManager
) :
    ViewModel() {

    private val _userMedsList = MutableLiveData<List<UserMedsEntity>>(mutableListOf())

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

    fun getMedsShareText(): String {
        val medsList = _userMedsList.value ?: return ""

        val now = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(now.time)

        val builder = StringBuilder()
        builder.append("Hey, here's my medicine history from PillWatch as of today, $formattedDate! \n\n ")
        for (med in medsList) {
            builder.append("No. ${medsList.indexOf(med) + 1} ")
            builder.append(med.tradeName)
            if (med.concentration != null && med.concentration != "") {
                builder.append(" ")
                builder.append(med.concentration)
            }
            builder.append("\n")
        }

        return builder.toString()
    }
}