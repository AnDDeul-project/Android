package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.ModifyProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface ModifyProfileInterface {
    @Multipart
    @PATCH("/home/user/profile")
    fun modifyProfile(
        @Part("nickname") nickname : RequestBody,
        @Part image : MultipartBody.Part?
    ) : Call<ModifyProfileResponse>
}