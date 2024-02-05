package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.UserProfileDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserProfileInterface {
    @GET("/home/user/{snsId}/profile")
    fun getUserProfile(
        @Path("snsId") snsId : String
    ): Call<UserProfileDTO>
}