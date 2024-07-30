package com.umc.anddeul.alarm

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface DeviceTokenInterface {
    @PUT("/push/putToken")
    fun putDeviceToken(
        @Body deviceToken: DeviceTokenRequest
    ): Call<ResponseBody>
}