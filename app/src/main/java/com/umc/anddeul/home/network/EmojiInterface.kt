package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.EmojiDTO
import com.umc.anddeul.home.model.EmojiRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface EmojiInterface {
    @POST("/home/posts/{postIdx}/emoji")
    fun getEmoji(
        @Path("postIdx") postIdx : Int, @Body emojiRequest: EmojiRequest
    ): Call<EmojiDTO>
}