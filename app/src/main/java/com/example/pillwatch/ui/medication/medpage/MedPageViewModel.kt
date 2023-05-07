package com.example.pillwatch.ui.medication.medpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MedPageViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository
): ViewModel() {

    private val _medEntity = MutableLiveData<UserMedsEntity?>()
    val medEntity: LiveData<UserMedsEntity?>
        get() = _medEntity

    fun getMedEntity(id: Long) {
        viewModelScope.launch {
            _medEntity.value = withContext(Dispatchers.IO) {
                userMedsRepository.getMedById(id)
            }
        }
    }
}