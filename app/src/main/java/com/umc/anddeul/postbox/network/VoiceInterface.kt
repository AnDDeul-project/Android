package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.VoiceRequest
import com.umc.anddeul.postbox.model.VoiceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VoiceInterface {
    @Multipart
    @POST("/mail/voice")
    fun voiceSend(
        @Header("Authorization") accessToken: String,
        @Part("member") member: RequestBody,
        @Part("question") question: RequestBody,
        @Part record: MultipartBody.Part
    ) : Call<VoiceResponse>
}