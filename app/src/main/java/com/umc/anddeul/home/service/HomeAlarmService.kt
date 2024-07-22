package com.umc.anddeul.home.service

import android.util.Log
import com.umc.anddeul.home.model.HomeAlarmResponse
import com.umc.anddeul.home.network.HomeAlarmInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeAlarmService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val questionService = retrofit.create(HomeAlarmInterface::class.java)

    fun alarmHome(accessToken: String, callback: (HomeAlarmResponse?) -> Unit) {
        val call = questionService.homeAlarm("Bearer $accessToken")
        call.enqueue(object : Callback<HomeAlarmResponse> {
            override fun onResponse(call: Call<HomeAlarmResponse>, response: Response<HomeAlarmResponse>) {
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

            override fun onFailure(call: Call<HomeAlarmResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}