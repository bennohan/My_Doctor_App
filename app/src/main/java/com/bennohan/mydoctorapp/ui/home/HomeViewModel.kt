package com.bennohan.mydoctorapp.ui.home

import androidx.lifecycle.viewModelScope
import com.bennohan.mydoctorapp.api.ApiService
import com.bennohan.mydoctorapp.base.BaseViewModel
import com.bennohan.mydoctorapp.data.bannerSlider.BannerSlider
import com.bennohan.mydoctorapp.data.category.Category
import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.bennohan.mydoctorapp.data.subdistrict.Subdistrict
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
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val session: CoreSession,
    private val gson: Gson,
    private val userDao: UserDao
) : BaseViewModel() {

    private var _listDoctor = MutableSharedFlow<List<Doctor?>>()
    var listDoctor = _listDoctor.asSharedFlow()

    private var _listDoctorFilter = MutableSharedFlow<List<Doctor?>>()
    var listDoctorFilter = _listDoctorFilter.asSharedFlow()

    private var _listSubdistrict = MutableSharedFlow<List<Subdistrict?>>()
    var listSubdistrict = _listSubdistrict.asSharedFlow()

    private var _listCategory = MutableSharedFlow<List<Category?>>()
    var listCategory = _listCategory.asSharedFlow()

    private var _bannerSlider = MutableSharedFlow<List<BannerSlider>>()
    var listBannerSlider = _bannerSlider.asSharedFlow()

    fun getDoctor(
        ) = viewModelScope.launch {
        ApiObserver({ apiService.getDoctor() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Doctor>(gson)
                    _listDoctor.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("data success"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getDoctorFilter(
        subdistrictId: String,
        categoryId: String?
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getDoctorFilter(subdistrictId, categoryId) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Doctor>(gson)
                    _listDoctorFilter.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess("data success"))

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getSubdistricts(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getSubdistricts() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Subdistrict>(gson)
                    _listSubdistrict.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getCategories(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getCategories() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Category>(gson)
                    _listCategory.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }

    fun getBannerSlider(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getBanner() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<BannerSlider>(gson)
                    _bannerSlider.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)

                    _apiResponse.emit(ApiResponse().responseError())

                }
            })
    }


}