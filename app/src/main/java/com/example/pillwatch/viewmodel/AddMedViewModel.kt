package com.example.pillwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.repository.MedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMedViewModel(medsDao: MedsDao, application: Application) : AndroidViewModel(application) {

    val medName = MutableLiveData("")

    private val repository = MedsRepository(medsDao)

    private val _medsList = MutableLiveData<LiveData<List<MedsEntity>>>()
    val medsList: LiveData<LiveData<List<MedsEntity>>>
        get() = _medsList

     val searchList : MutableList<String> = mutableListOf()


    fun getAllMeds() {
        viewModelScope.launch {
            val medsListFromDb = withContext(Dispatchers.IO) {
                repository.getAllMeds()
            }
            if (medsListFromDb != null) {
                _medsList.value = medsListFromDb
            }
        }
    }

    fun searchMedName(medName: String) {
        viewModelScope.launch {
            val medsSearch = withContext(Dispatchers.IO) {
                repository.searchMedsWithName(medName)
            }
            if (medsSearch != null) {
                searchList.addAll(medsSearch)
            }
        }
    }



    fun navigateToMedication(navController: NavController) {
        navController.navigate(R.id.action_addMedFragment_to_medicationFragment)
    }

}