package com.umc.anddeul.start.network

import com.umc.anddeul.start.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenInterface {
    @POST("/auth/token")
    fun getRequests(
        @Header("Authorization") accessToken: String
    ): Call<TokenResponse>
}