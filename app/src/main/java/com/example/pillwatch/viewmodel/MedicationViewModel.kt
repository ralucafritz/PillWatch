package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.UserMedsDao
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MedicationViewModel(userMedsDao: UserMedsDao, application: Application) :
    AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    private val userMedsRepository = UserMedsRepository(userMedsDao)

    suspend fun getMedsList(): List<UserMedsEntity>? {
        return withContext(Dispatchers.IO) {
            val userId = context.getPreference("id", 0L).toString().toLong() ?: -1L
            if (userId != -1L) {
                userMedsRepository.getAllMedsForUser(userId)
            } else {
                null
            }
        }
    }

    fun navigateToAddAMed(navController: NavController) {
        navController.navigate(R.id.action_medicationFragment_to_addMedFragment)
    }

}