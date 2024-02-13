package com.umc.anddeul.home.network

import com.umc.anddeul.home.model.PostDelete
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Call


interface PostDeleteInterface {
    @DELETE("/home/posts/{postIdx}")
    fun deletePost(@Path("postIdx") postIdx : Int): Call<PostDelete>
}