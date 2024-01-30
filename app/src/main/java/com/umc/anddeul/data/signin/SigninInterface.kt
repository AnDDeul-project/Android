package com.umc.anddeul.data.signin

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface SigninInterface {
    @POST("/auth/kakao/signin")
    fun getSignin(
        @Header("Authorization") accessToken: String
    ): Call<SigninResponse>
}