package com.bennohan.mydoctorapp.ui.register

import androidx.lifecycle.viewModelScope
import com.bennohan.mydoctorapp.api.ApiService
import com.bennohan.mydoctorapp.base.BaseViewModel
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.data.User
import com.bennohan.mydoctorapp.data.UserDao
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) : BaseViewModel() {

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        passwordConfirmation: String,
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver({ apiService.register(name, email, phone, password, passwordConfirmation) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}