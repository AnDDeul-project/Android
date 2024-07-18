package com.umc.anddeul.mypage.network

import retrofit2.Call
import com.umc.anddeul.mypage.model.SelectLeaderDTO
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SelectLeaderInterface {
    @PATCH("/family/leader/{userid}")
    fun selectLeader(
        @Path("userid") userid: String
    ): Call<SelectLeaderDTO>
}