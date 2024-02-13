package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.TextRequest
import com.umc.anddeul.postbox.model.TextResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TextInterface {
    @POST("/mail/text")
    fun textSend(
        @Header("Authorization") accessToken: String,
        @Body request: TextRequest
    ): Call<TextResponse>
}