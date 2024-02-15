package com.umc.anddeul.postbox.network

import com.umc.anddeul.postbox.model.FamilyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface FamilyInterface {
    @GET("/home/family/members")
    fun familyList(
        @Header("Authorization") accessToken: String,
    ) : Call<FamilyResponse>
}