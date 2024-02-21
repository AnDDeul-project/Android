package com.umc.anddeul.postbox.service

import android.util.Log
import com.umc.anddeul.postbox.model.TextRequest
import com.umc.anddeul.postbox.model.TextResponse
import com.umc.anddeul.postbox.network.TextInterface
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TextService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val textService = retrofit.create(TextInterface::class.java)

    fun sendText(accessToken: String, textRequest: TextRequest, callback: (TextResponse?) -> Unit) {

        val call = textService.textSend(
            "Bearer $accessToken", textRequest)
        call.enqueue(object : Callback<TextResponse> {
            override fun onResponse(call: Call<TextResponse>, response: Response<TextResponse>) {
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

            override fun onFailure(call: Call<TextResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}