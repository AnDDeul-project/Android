package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.PostboxAlarmResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface PostboxAlarmInterface {
    // 랜덤 질문
    @GET("/alarm/postbox")
    fun postboxAlarm(
        @Header("Authorization") accessToken: String
    ): Call<PostboxAlarmResponse>
}