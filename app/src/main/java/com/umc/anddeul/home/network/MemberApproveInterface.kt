package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.MemberApproveDTO
import retrofit2.Call
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberApproveInterface {
    @PUT("/home/family/{userId}")
    fun approveMember(
        @Path("userId") userId : String
    ) : Call<MemberApproveDTO>
}