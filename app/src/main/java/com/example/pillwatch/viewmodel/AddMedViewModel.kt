package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.datasource.local.UserMedsDao
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMedViewModel(
    medsDao: MedsDao,
    userMedsDao: UserMedsDao,
    application: Application
) : AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    val medName = MutableLiveData("")

    private val medsRepository = MedsRepository(medsDao)
    private val userMedsRepository = UserMedsRepository(userMedsDao)

    private val _selectedPairNumber = MutableLiveData<Int>()
    val selectedPairNumber: LiveData<Int>
        get() = _selectedPairNumber

    private val _selectedPair = MutableLiveData<MedsEntity>()
    val selectedPair: LiveData<MedsEntity>
        get() = _selectedPair

    private val _pairList = MutableLiveData<List<MedsEntity>>()
    val pairList: LiveData<List<MedsEntity>>
        get() = _pairList

    val searchList: MutableList<String> = mutableListOf()


    fun searchMedName(medName: String) {
        viewModelScope.launch {
            val medsSearch = withContext(Dispatchers.IO) {
                medsRepository.searchMedsWithName(medName)
            }
            if (medsSearch != null) {
                _pairList.value = medsSearch
                val stringList = mutableListOf<String>()
                _pairList.value!!.forEach {
                    stringList.add(it.tradeName)
                }
                searchList.addAll(stringList)
            }
        }
    }

    fun getPairAtPosition(position: Int) {
        _selectedPairNumber.value = position
        _selectedPair.value = _pairList.value?.get(position)
    }

    suspend fun addMedToUser() {
        val userId = context.getPreference("id", 0L).toString().toLong()
        val medId: Long
        val name: String
        if (_selectedPair.value != null) {
            medId = _selectedPair.value!!.id
            name = _selectedPair.value!!.tradeName
            withContext(Dispatchers.IO) {
                userMedsRepository.insert(
                    UserMedsEntity(
                        0L,
                        name,
                        userId,
                        medId
                    )
                )
            }
        }
    }

    fun navigateToMedication(navController: NavController) {
        navController.navigate(R.id.action_addMedFragment_to_medicationFragment)
    }

}