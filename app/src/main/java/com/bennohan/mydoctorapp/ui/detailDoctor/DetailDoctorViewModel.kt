package com.bennohan.mydoctorapp.ui.detailDoctor

import androidx.lifecycle.viewModelScope
import com.bennohan.mydoctorapp.api.ApiService
import com.bennohan.mydoctorapp.base.BaseViewModel
import com.bennohan.mydoctorapp.data.Doctor
import com.bennohan.mydoctorapp.data.UserDao
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailDoctorViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    private var _detailDoctor = MutableSharedFlow<Doctor?>()
    var detailDoctor = _detailDoctor.asSharedFlow()

    private var _doctorLike : String? = null
    var doctorLike = _doctorLike

    fun getDoctor(
        idDoctor: String,
        ) = viewModelScope.launch {
        ApiObserver({ apiService.getDoctorDetail(idDoctor) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Doctor>(gson)
                    _detailDoctor.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }
    fun saveDoctor(
        idDoctor: String,
        ) = viewModelScope.launch {
        ApiObserver({ apiService.saveUnsave(idDoctor) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Saved"))
                    val likeDisliked = response.getJSONObject(ApiCode.DATA).getString("status_favorite")
//                    _doctorLike = likeDisliked

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun createReservations(
        idDoctor: String,
        timeReservation : String,
        remarksNote : String
        ) = viewModelScope.launch {
        ApiObserver({ apiService.createReservation(idDoctor,timeReservation,remarksNote) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Reservation Created"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}