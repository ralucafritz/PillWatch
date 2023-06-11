package com.example.pillwatch.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userManager: UserManager,
    private val userRepository: UserRepository,
    private val userMedsRepository: UserMedsRepository
): ViewModel() {

    val username: String
        get() = userManager.username

    val email: String
        get() = userManager.email

    private val _medsCount = MutableLiveData<Int?>()
    val medsCount: LiveData<Int?>
        get() = _medsCount

    fun getMedsCount() {
        viewModelScope.launch {
            _medsCount.value = withContext(Dispatchers.IO) {
                userMedsRepository.getActiveMedCountByUserId(userManager.id)
            }
        }
    }

    fun updateUsername(updatedName: String) {
        userManager.setUsername(updatedName)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = userManager.id
                userRepository.updateUsername(userId, updatedName)
            }
        }
    }
}