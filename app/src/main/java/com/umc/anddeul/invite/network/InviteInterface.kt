package com.umc.anddeul.invite.network

import com.umc.anddeul.invite.model.AddFamilyRequest
import com.umc.anddeul.invite.model.AddFamilyResponse
import com.umc.anddeul.invite.model.FamilyInfoResponse
import com.umc.anddeul.invite.model.NewFamilyRequest
import com.umc.anddeul.invite.model.NewFamilyResponse
import com.umc.anddeul.start.signin.model.SigninResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InviteInterface {
    // 가족 생성
    @PUT("/random/new")
    fun familyCreate(
        @Header("Authorization") accessToken: String,
        @Body familyName: NewFamilyRequest
    ): Call<NewFamilyResponse>

    // 가족 요청
    @PUT("/family/add")
    fun familyAdd(
        @Header("Authorization") accessToken: String,
        @Body request: AddFamilyRequest
    ): Call<AddFamilyResponse>

    // 가족 검색
    @GET("/family/info/{family_code}")
    fun familySearch(
        @Header("Authorization") accessToken: String,
        @Path("family_code") familyCode: String
    ): Call<FamilyInfoResponse>
}