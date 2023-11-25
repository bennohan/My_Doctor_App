package com.bennohan.mydoctorapp.ui.history

import androidx.lifecycle.viewModelScope
import com.bennohan.mydoctorapp.api.ApiService
import com.bennohan.mydoctorapp.base.BaseViewModel
import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.bennohan.mydoctorapp.data.historyDoctor.HistoryReservation
import com.bennohan.mydoctorapp.data.user.UserDao
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) :  BaseViewModel() {

    private var _listHistoryOrder = MutableSharedFlow<List<HistoryReservation?>>()
    var listHistoryOrder = _listHistoryOrder.asSharedFlow()

    private var _doctorData = MutableSharedFlow<Doctor?>()
    var doctorData = _doctorData.asSharedFlow()

    //TODO FORMAT HISTORY BERBEDA ??
    //TODO APAKAH HRUS MEMBUAT DATACLASS BARU UNTUK MENAMPUNG RESPONSE HISTORY ??
    fun getHistoryOrder(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getHistory() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<HistoryReservation>(gson)
//                    val dataDoctor = response.getJSONArray(ApiCode.DATA)
                    _listHistoryOrder.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}