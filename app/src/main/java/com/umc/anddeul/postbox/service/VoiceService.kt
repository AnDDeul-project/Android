package com.umc.anddeul.postbox.service

import com.umc.anddeul.postbox.model.VoiceRequest
import com.umc.anddeul.postbox.model.VoiceResponse
import com.umc.anddeul.postbox.network.VoiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VoiceService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val voiceService = retrofit.create(VoiceInterface::class.java)

    fun sendVoice(accessToken: String, textRequest: VoiceRequest, callback: (VoiceResponse?) -> Unit) {
        val call = voiceService.voiceSend("Bearer $accessToken", textRequest)
        call.enqueue(object : Callback<VoiceResponse> {
            override fun onResponse(call: Call<VoiceResponse>, response: Response<VoiceResponse>) {
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        callback(response.body())
                    }
                    500 -> {
                        callback(response.body())
                    }
                    4001 -> {
                        callback(response.body())
                    }
                    else -> {
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<VoiceResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}