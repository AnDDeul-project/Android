package com.umc.anddeul.postbox.service

import android.util.Log
import com.umc.anddeul.postbox.model.TodayMailResponse
import com.umc.anddeul.postbox.network.MailInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MailService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mailService = retrofit.create(MailInterface::class.java)

    fun todayMail(accessToken: String, today: String, callback: (TodayMailResponse?) -> Unit) {
        val call = mailService.mailToday("Bearer $accessToken", today)
        call.enqueue(object : Callback<TodayMailResponse> {
            override fun onResponse(call: Call<TodayMailResponse>, response: Response<TodayMailResponse>) {
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

            override fun onFailure(call: Call<TodayMailResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}