package com.umc.anddeul.invite.service

import android.util.Log
import com.umc.anddeul.invite.model.NewFamilyRequest
import com.umc.anddeul.invite.model.NewFamilyResponse
import com.umc.anddeul.invite.network.InviteInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyNewService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyNewService = retrofit.create(InviteInterface::class.java)

    fun createFamily(accessToken: String, familyNameTemp: String, callback: (NewFamilyResponse?) -> Unit) {
        val familyName = NewFamilyRequest(familyNameTemp)
        val call = familyNewService.familyCreate("Bearer $accessToken", familyName)
        call.enqueue(object : Callback<NewFamilyResponse> {
            override fun onResponse(call: Call<NewFamilyResponse>, response: Response<NewFamilyResponse>) {
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

            override fun onFailure(call: Call<NewFamilyResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}