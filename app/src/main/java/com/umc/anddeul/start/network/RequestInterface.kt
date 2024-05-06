package com.umc.anddeul.start.network

import com.umc.anddeul.start.model.RequestResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RequestInterface {
    @GET("/family/request")
    fun getRequests(
        @Header("Authorization") accessToken: String
    ): Call<RequestResponse>
}