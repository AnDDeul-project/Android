package com.umc.anddeul.postbox.service

import android.util.Log
import com.umc.anddeul.postbox.model.ReadMailResponse
import com.umc.anddeul.postbox.network.ReadMailInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReadMailService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val readMailService = retrofit.create(ReadMailInterface::class.java)

    fun readMail(accessToken: String, idx: Int, callback: (ReadMailResponse?) -> Unit) {
        val call = readMailService.mailRead("Bearer $accessToken", idx)
        call.enqueue(object : Callback<ReadMailResponse> {
            override fun onResponse(call: Call<ReadMailResponse>, response: Response<ReadMailResponse>) {
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

            override fun onFailure(call: Call<ReadMailResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}