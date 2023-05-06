package com.example.pillwatch.ui.home

import androidx.lifecycle.ViewModel
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import javax.inject.Inject

@LoggedUserScope
class HomeViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {


}