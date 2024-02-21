package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.QuestionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface QuestionInterface {
    // 랜덤 질문
    @GET("/mail/question")
    fun questionRandom(
        @Header("Authorization") accessToken: String
    ): Call<QuestionResponse>
}