package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.OnePostDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OnePostInterface {
    @GET("/home/posts/one/{postIdx}")
    fun onePost(
        @Path("postIdx") postIdx : Int
    ) : Call<OnePostDTO>
}