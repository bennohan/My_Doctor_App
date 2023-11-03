package com.bennohan.mydoctorapp.data

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Doctor(
    @Expose
    @SerializedName("address")
    val address: String,
    @Expose
    @SerializedName("category")
    val category: Category,
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("images")
    val images: List<String>,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("photo")
    val photo: String,
    @Expose
    @SerializedName("subdistrict")
    val subdistrict: Subdistrict
)