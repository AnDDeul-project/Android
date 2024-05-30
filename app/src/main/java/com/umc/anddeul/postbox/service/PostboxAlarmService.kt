package com.umc.anddeul.postbox.service

import android.util.Log
import com.umc.anddeul.postbox.model.PostboxAlarmResponse
import com.umc.anddeul.postbox.network.PostboxAlarmInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostboxAlarmService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val questionService = retrofit.create(PostboxAlarmInterface::class.java)

    fun alarmPostbox(accessToken: String, callback: (PostboxAlarmResponse?) -> Unit) {
        val call = questionService.postboxAlarm("Bearer $accessToken")
        call.enqueue(object : Callback<PostboxAlarmResponse> {
            override fun onResponse(call: Call<PostboxAlarmResponse>, response: Response<PostboxAlarmResponse>) {
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

            override fun onFailure(call: Call<PostboxAlarmResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}