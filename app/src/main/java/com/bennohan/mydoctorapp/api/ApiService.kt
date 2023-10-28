package com.bennohan.mydoctorapp.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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


}