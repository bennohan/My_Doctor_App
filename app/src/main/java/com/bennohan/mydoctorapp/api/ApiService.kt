package com.bennohan.mydoctorapp.api

import okhttp3.MultipartBody
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
    @POST("auth/register")
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

    @FormUrlEncoded
    @POST("user/profile")
    suspend fun updateProfile(
        @Field("name") name: String?,
        ): String

    @Multipart
    @POST("user/profile")
    suspend fun updateProfilePhoto(
        @Query("name") name: String?,
        @Part photo : MultipartBody.Part?
    ): String



    @GET("auth/me")
    suspend fun getProfile(
    ): String

    //Doctor
    @GET("docters")
    suspend fun getDoctor(
    ): String

    @GET("reservations")
    suspend fun getHistory(
    ): String

    @GET("docters/categories/")
    suspend fun getCategories(
    ): String

    @GET("promo-banners/")
    suspend fun getBanner(
    ): String

    @GET("docters/{id_docter}")
    suspend fun getDoctorDetail(
        @Path("id_docter") idDoctor: String?
    ): String

    @GET("docters/saved")
    suspend fun getDoctorSaved(): String

    //Reservation
    @FormUrlEncoded
    @POST("reservations")
    suspend fun createReservation(
        @Field("docter_id") doctorId: String?,
        @Field("time_reservation") timeReservation: String?,
        @Field("remarks") remarksNote: String?,
    ): String

    @GET("docters/filter")
    suspend fun getDoctorFilter(
        @Query("subdistrict_id") subdistrictId: String?,
        @Query("category_id") categoryId: String?,
    ): String

    @FormUrlEncoded
    @POST("docters/saved")
    suspend fun saveUnsave(
        @Field("docter_id") doctorId: String?,
    ): String


}