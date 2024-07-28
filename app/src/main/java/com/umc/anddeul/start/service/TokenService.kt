package com.umc.anddeul.start.service

import com.umc.anddeul.start.model.TokenResponse
import com.umc.anddeul.start.network.TokenInterface
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tokenService = retrofit.create(TokenInterface::class.java)

    fun requestToken(accessToken: String, callback: (TokenResponse?) -> Unit) {
        val call = tokenService.getRequests("Bearer $accessToken")

        call.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        val errorBodyString = response.errorBody()?.string()
                        val errorResponse =
                            retrofit.responseBodyConverter<TokenResponse>(
                                TokenResponse::class.java, arrayOfNulls(0))
                                .convert(ResponseBody.create(null, errorBodyString ?: ""))
                        callback(errorResponse)
                    }
                    else -> {
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}