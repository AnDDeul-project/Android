package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.VoiceRequest
import com.umc.anddeul.postbox.model.VoiceResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface VoiceInterface {
    @POST("/mail/voice")
    fun voiceSend(
        @Header("Authorization") accessToken: String,
        @Body request: VoiceRequest
    ) : Call<VoiceResponse>
}