package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.HomeAlarmResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeAlarmInterface {
    // 랜덤 질문
    @GET("/alarm/home")
    fun homeAlarm(
        @Header("Authorization") accessToken: String
    ): Call<HomeAlarmResponse>
}