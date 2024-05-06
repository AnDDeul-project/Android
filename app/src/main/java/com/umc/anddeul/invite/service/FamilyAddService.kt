package com.umc.anddeul.invite.service

import android.util.Log
import com.umc.anddeul.invite.model.AddFamilyRequest
import com.umc.anddeul.invite.model.AddFamilyResponse
import com.umc.anddeul.invite.network.InviteInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyAddService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyNewService = retrofit.create(InviteInterface::class.java)

    fun addFamily(accessToken: String, familyCodeTemp: String, callback: (AddFamilyResponse?) -> Unit) {
        val familyCodeNm = AddFamilyRequest(familyCodeTemp)
        val call = familyNewService.familyAdd("Bearer $accessToken", familyCodeNm)
        call.enqueue(object : Callback<AddFamilyResponse> {
            override fun onResponse(call: Call<AddFamilyResponse>, response: Response<AddFamilyResponse>) {
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

            override fun onFailure(call: Call<AddFamilyResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}