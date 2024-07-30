package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.TextRequest
import com.umc.anddeul.postbox.model.TextResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TextInterface {
    @POST("/mail/text")
    fun textSend(
        @Header("Authorization") accessToken: String,
        @Body request: TextRequest
    ): Call<TextResponse>
}