package com.umc.anddeul.start.service

import android.util.Log
import com.umc.anddeul.start.model.RequestResponse
import com.umc.anddeul.start.network.RequestInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val requestService = retrofit.create(RequestInterface::class.java)

    fun requestInfo(accessToken: String, callback: (RequestResponse?) -> Unit) {
        val call = requestService.getRequests("Bearer $accessToken")

        call.enqueue(object : Callback<RequestResponse> {
            override fun onResponse(call: Call<RequestResponse>, response: Response<RequestResponse>) {
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

            override fun onFailure(call: Call<RequestResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}