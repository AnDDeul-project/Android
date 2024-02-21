package com.umc.anddeul.mypage.network

import com.umc.anddeul.mypage.model.LogoutDTO
import retrofit2.Call
import retrofit2.http.POST

interface LogoutInterface {
    @POST("/auth/kakao/logout")
    fun logout(): Call<LogoutDTO>
}