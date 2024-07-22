package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.MemberResponse
import retrofit2.Call
import retrofit2.http.GET

interface MemberInterface {
    @GET("/home/family/members")
    fun memberList() : Call<MemberResponse>
}