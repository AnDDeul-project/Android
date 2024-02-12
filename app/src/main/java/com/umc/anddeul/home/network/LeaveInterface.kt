package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.LeaveDTO
import retrofit2.Call
import retrofit2.http.DELETE

interface LeaveInterface {
    @DELETE("/auth/kakao/unlink")
    fun leaveApp(): Call<LeaveDTO>
}