package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.TodayMailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MailInterface {
    // 오늘 편지 리스트
    @GET("/mail/all/{today}")
    fun mailToday(
        @Header("Authorization") accessToken: String,
        @Path("today") today: String
    ): Call<TodayMailResponse>
}