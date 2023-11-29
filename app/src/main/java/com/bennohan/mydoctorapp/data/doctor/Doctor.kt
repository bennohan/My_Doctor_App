package com.bennohan.mydoctorapp.data.doctor

import androidx.room.Entity
import com.bennohan.mydoctorapp.data.category.Category
import com.bennohan.mydoctorapp.data.subdistrict.Subdistrict
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
    @SerializedName("description")
    val description: String,
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
    val photo: String?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("phone")
    val phone: String?,
    @Expose
    @SerializedName("subdistrict")
    val subdistrict: Subdistrict,
    @Expose
    @SerializedName("status_opration")
    val statusOperation: Boolean,
    @Expose
    @SerializedName("save_by_you")
    val saveByYou: Boolean,
    )