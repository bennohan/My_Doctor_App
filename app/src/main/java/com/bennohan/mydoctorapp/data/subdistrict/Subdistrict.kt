package com.bennohan.mydoctorapp.data.subdistrict

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Subdistrict(
    @Expose
    @SerializedName("id")
    val id: String,
    @Expose
    @SerializedName("name")
    val name: String,
    var selected: Boolean = false
) {
    override fun toString(): String {
        return name.toString()
    }
}