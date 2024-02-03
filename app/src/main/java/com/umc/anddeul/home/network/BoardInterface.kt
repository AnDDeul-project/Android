package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.BoardResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BoardInterface {
    @Multipart
    @POST("/home/board")
    fun homeBoard(
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Call<BoardResponse>
}

