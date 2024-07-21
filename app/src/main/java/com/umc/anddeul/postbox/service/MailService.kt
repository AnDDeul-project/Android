package com.umc.anddeul.postbox.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umc.anddeul.postbox.model.TodayMailResponse
import com.umc.anddeul.postbox.network.MailInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MailService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mailService = retrofit.create(MailInterface::class.java)

    fun todayMail(context: Context, accessToken: String, today: String, callback: (TodayMailResponse?) -> Unit) {
        val call = mailService.mailToday("Bearer $accessToken", today)
        call.enqueue(object : Callback<TodayMailResponse> {
            override fun onResponse(call: Call<TodayMailResponse>, response: Response<TodayMailResponse>) {
                Log.d("Postbox MailService code", "${response.code()}")
                Log.d("Postbox MailService body", "${response.body()}")
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

            override fun onFailure(call: Call<TodayMailResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}