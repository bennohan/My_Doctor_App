package com.bennohan.mydoctorapp.data.user


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey
    @Expose
    @SerializedName("id_room")
    val idRoom: Int,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("email")
    val email: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("phone")
    val phone: String?,
    @Expose
    @SerializedName("photo")
    val photo: String?
)