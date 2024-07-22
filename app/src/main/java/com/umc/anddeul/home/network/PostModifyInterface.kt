package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.ModifyRequest
import com.umc.anddeul.home.model.PostModifyDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PostModifyInterface {
    @PATCH("/home/posts/{postIdx}")
    fun modifyPost(
        @Path("postIdx") postIdx : Int, @Body modifyRequest: ModifyRequest
    ): Call<PostModifyDTO>
}