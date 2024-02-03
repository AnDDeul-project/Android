package com.umc.anddeul.start.signin.network

import com.umc.anddeul.start.signin.model.SigninResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface SigninInterface {
    @POST("/auth/kakao/signin")
    fun getSignin(
        @Header("Authorization") accessToken: String
    ): Call<SigninResponse>
}