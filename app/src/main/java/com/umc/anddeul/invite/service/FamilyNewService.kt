package com.umc.anddeul.invite.service

import com.umc.anddeul.invite.network.InviteInterface
import com.umc.anddeul.start.signin.model.SigninResponse
import com.umc.anddeul.start.signin.network.SigninInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyNewService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyNewService = retrofit.create(InviteInterface::class.java)

    fun createFamily(accessToken: String, callback: (SigninResponse?) -> Unit) {
        val call = familyNewService.familyCreate("Bearer $accessToken")

        call.enqueue(object : Callback<SigninResponse> {
            override fun onResponse(call: Call<SigninResponse>, response: Response<SigninResponse>) {
                when (response.code()) {
                    200 -> {
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

            override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}