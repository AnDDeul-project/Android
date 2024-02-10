package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.LogoutDTO
import retrofit2.Call
import retrofit2.http.POST

interface LogoutInterface {
    @POST("/auth/kakao/logout")
    fun logout(): Call<LogoutDTO>
}