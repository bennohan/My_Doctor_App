package com.bennohan.mydoctorapp.data.historyDoctor


import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HistoryReservation(
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("created_by")
    val createdBy: String?,
    @Expose
    @SerializedName("docter")
    val docter: Doctor?,
    @Expose
    @SerializedName("docter_id")
    val docterId: String?,
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("queue_number")
    val queueNumber: Int?,
    @Expose
    @SerializedName("remark_cancel")
    val remarkCancel: Any?,
    @Expose
    @SerializedName("remarks")
    val remarks: String?,
    @Expose
    @SerializedName("status")
    val status: String?,
    @Expose
    @SerializedName("time_arrival")
    val timeArrival: String?,
    @Expose
    @SerializedName("time_reservation")
    val timeReservation: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
)