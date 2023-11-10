package com.bennohan.mydoctorapp.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerSlider(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?
)