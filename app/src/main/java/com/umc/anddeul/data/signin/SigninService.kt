package com.umc.anddeul.data.signin

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SigninService {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val signinService = retrofit.create(SigninInterface::class.java)

    fun executeSignIn(accessToken: String, callback: (SigninResponse?) -> Unit) {
        val call = signinService.getSignin("Bearer $accessToken")

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