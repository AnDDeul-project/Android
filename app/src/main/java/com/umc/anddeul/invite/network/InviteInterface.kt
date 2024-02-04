package com.umc.anddeul.invite.network

import com.umc.anddeul.invite.model.FamilyInfoResponse
import com.umc.anddeul.start.signin.model.SigninResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteInterface {
    // 가족 생성
    @POST("/random/new")
    fun familyCreate(
        @Header("Authorization") accessToken: String
    ): Call<SigninResponse>

    // 가족 요청
//    @PUT("/family/add")
//    fun familyAdd(
//        @Header("Authorization") accessToken: String,
//        @Body request: FamilyAddRequest
//    ): Call<SigninResponse>

    // 가족 검색
    @GET("/family/info/{family_code}")
    fun familySearch(
        @Header("Authorization") accessToken: String,
        @Path("family_code") familyCode: String
    ): Call<FamilyInfoResponse>
}