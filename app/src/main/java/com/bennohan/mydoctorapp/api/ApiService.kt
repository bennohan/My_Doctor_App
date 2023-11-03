package com.bennohan.mydoctorapp.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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

    @GET("auth/me")
    suspend fun getProfile(
    ): String

    @GET("docters")
    suspend fun getDoctor(
    ): String

}