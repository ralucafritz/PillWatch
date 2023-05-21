package com.example.pillwatch.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pillwatch.di.LoggedUserScope

import androidx.lifecycle.ViewModel
import com.example.pillwatch.R
import com.example.pillwatch.user.UserManager
import javax.inject.Inject

@LoggedUserScope
class SettingsViewModel @Inject constructor(
    private val userManager: UserManager
    ) : ViewModel() {

}
