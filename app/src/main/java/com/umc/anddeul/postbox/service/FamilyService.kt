package com.umc.anddeul.postbox.service

import android.util.Log
import com.umc.anddeul.postbox.model.FamilyResponse
import com.umc.anddeul.postbox.network.FamilyInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyService = retrofit.create(FamilyInterface::class.java)

    fun getFamilyList(accessToken: String, callback: (FamilyResponse?) -> Unit) {
        val call = familyService.familyList("Bearer $accessToken")

        call.enqueue(object : Callback<FamilyResponse> {
            override fun onResponse(call: Call<FamilyResponse>, response: Response<FamilyResponse>) {
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
                    else -> {
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<FamilyResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}