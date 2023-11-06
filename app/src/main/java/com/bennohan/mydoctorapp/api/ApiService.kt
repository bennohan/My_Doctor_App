package com.bennohan.mydoctorapp.api

import retrofit2.http.*

interface ApiService {

    //AUTH
    //login
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email_or_phone") emailOrPhone: String?,
        @Field("password") password: String?,
    ): String

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("phone") phone: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") passwordConfirmation: String?,
        @Field("subdistrict_id") subdistrictId: String?,
    ): String

    @DELETE("auth/logout")
    suspend fun logout(): String

    @GET("subdistricts/")
    suspend fun getSubdistricts(
    ): String

    @GET("auth/me")
    suspend fun getProfile(
    ): String

    //Doctor
    @GET("docters")
    suspend fun getDoctor(
    ): String

    @GET("docters/{id_docter}")
    suspend fun getDoctorDetail(
        @Path("id_docter") idDoctor: String?
    ): String

    @GET("docters/saved")
    suspend fun getDoctorSaved(): String

    //Reservation
    @FormUrlEncoded
    @POST("reservations/")
    suspend fun createReservation(
        @Field("docter_id") doctorId: String?,
        @Field("time_reservation") timeReservation: String?,
        @Field("remarks") remarksNote: String?,
    ): String

}