package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.ReadMailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ReadMailInterface {
    // 오늘 편지 리스트
    @GET("/mail/one/{idx}")
    fun mailRead(
        @Header("Authorization") accessToken: String,
        @Path("idx") today: Int
    ): Call<ReadMailResponse>
}