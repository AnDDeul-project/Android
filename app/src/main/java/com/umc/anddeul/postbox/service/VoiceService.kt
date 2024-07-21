package com.umc.anddeul.postbox.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umc.anddeul.postbox.model.VoiceRequest
import com.umc.anddeul.postbox.model.VoiceResponse
import com.umc.anddeul.postbox.network.VoiceInterface
import com.umc.anddeul.start.StartActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VoiceService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val voiceService = retrofit.create(VoiceInterface::class.java)

    fun sendVoice(context: Context, accessToken: String, member: RequestBody, question: RequestBody, record: MultipartBody.Part, callback: (VoiceResponse?) -> Unit) {
        val call = voiceService.voiceSend("Bearer $accessToken", member, question, record)
        call.enqueue(object : Callback<VoiceResponse> {
            override fun onResponse(call: Call<VoiceResponse>, response: Response<VoiceResponse>) {
                Log.d("Postbox VoiceService code", "${response.code()}")
                Log.d("Postbox VoiceService body", "${response.body()}")
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        val startIntent = Intent(context, StartActivity::class.java)
                        context.startActivity(startIntent)
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