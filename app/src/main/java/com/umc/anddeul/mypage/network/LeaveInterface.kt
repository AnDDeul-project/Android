package com.umc.anddeul.mypage.network

import com.umc.anddeul.mypage.model.LeaveDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE

interface LeaveInterface {
    @DELETE("/auth/kakao/unlink")
    fun leaveApp(
        @Body content: String
    ): Call<LeaveDTO>
}