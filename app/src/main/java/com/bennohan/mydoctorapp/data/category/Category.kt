package com.bennohan.mydoctorapp.data.category

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Category(
    @Expose
    @SerializedName("created_at")
    val created_at: String,
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("updated_at")
    val updated_at: String,
    var selected: Boolean = false
){
    override fun toString(): String {
        return name.toString()
    }
}