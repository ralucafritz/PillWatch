package com.example.pillwatch.ui.medication

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@LoggedUserScope
class MedicationViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val userManager: UserManager
) :
    ViewModel() {

    suspend fun getMedsList(): List<UserMedsEntity>? {
        return withContext(Dispatchers.IO) {
            val userId = userManager.id
            if (userId != -1L) {
                userMedsRepository.getAllMedsForUser(userId)
            } else {
                null
            }
        }
    }
}