package com.umc.anddeul.invite.service

import android.util.Log
import com.umc.anddeul.invite.network.InviteInterface
import com.umc.anddeul.invite.model.FamilyInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyInfoService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyInfoService = retrofit.create(InviteInterface::class.java)

    fun searchFamily(accessToken: String, familyCode: String, callback: (FamilyInfoResponse?) -> Unit) {
        val call = familyInfoService.familySearch("Bearer $accessToken", familyCode)

        call.enqueue(object : Callback<FamilyInfoResponse> {
            override fun onResponse(call: Call<FamilyInfoResponse>, response: Response<FamilyInfoResponse>) {
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        callback(response.body())
                    }
                    409 -> {
                        callback(response.body())
                    }
                    410 -> {
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

            override fun onFailure(call: Call<FamilyInfoResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}