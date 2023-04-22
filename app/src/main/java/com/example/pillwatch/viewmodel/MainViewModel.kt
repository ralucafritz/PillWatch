package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.repository.MedsDataRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    medsDao: MedsDao,
    metadataDao: MetadataDao,
    userDao: UserDao,
    application: Application)
    : AndroidViewModel(application) {

    private val repositoryMeds = MedsDataRepository(medsDao)
    private val repositoryMetadata = MetadataRepository(metadataDao)
    private val repositoryUser = UserRepository(userDao)

    private val _currentFragmentId = MutableLiveData<Int>()
    val currentFragmentId: LiveData<Int>
        get() = _currentFragmentId

    private val context: Context by lazy { application.applicationContext }

    fun setCurrentFragmentId(id: Int) {
        _currentFragmentId.value = id
    }

    fun logout() {
        context.setLoggedInStatus(false)
        context.setPreference("email", "")
        context.setPreference("id", 0L)
        context.setPreference("username", "")
    }

    fun clear() {
        viewModelScope.launch{
            cleanMedsData()
            cleanMetadata()
            cleanUsers()
        }
        logout()
    }

    private suspend fun cleanMedsData() {
        withContext(Dispatchers.IO) {
            repositoryMeds.clear()
        }
    }


    private suspend fun cleanMetadata() {
        withContext(Dispatchers.IO) {
            repositoryMetadata.clear()
        }
    }


    private suspend fun cleanUsers() {
        withContext(Dispatchers.IO) {
            repositoryUser.clear()
        }
    }
}