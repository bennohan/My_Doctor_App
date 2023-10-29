package com.bennohan.mydoctorapp.ui.register

import com.bennohan.mydoctorapp.api.ApiService
import com.bennohan.mydoctorapp.base.BaseViewModel
import com.bennohan.mydoctorapp.data.UserDao
import com.crocodic.core.data.CoreSession
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

}