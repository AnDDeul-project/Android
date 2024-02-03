package com.umc.anddeul.home.service

import PostsInterface
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.umc.anddeul.home.model.Post
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PostService(context: Context) {

    // 저장된 토큰 가져오기
    val spf: SharedPreferences = context.getSharedPreferences("myToken", Context.MODE_PRIVATE)
    // private val token = spf.getString("jwtToken", "")
    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNjY4MzkxMH0.ncVxzwxBVaiMegGD0VU5pI5i9GJjhrU8kUIYtQrSLSg"

    val retrofitBearer = Retrofit.Builder()
        .baseUrl("http://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token.orEmpty())
                        .build()
                    Log.d("retrofitBearer", "Token: " + token.orEmpty())
                    chain.proceed(request)
                }
                .build()
        )
        .build()

    val postService = retrofitBearer.create(PostsInterface::class.java)

    fun getPost(callback: (Post?) -> Unit) {
        Log.e("getPost", "loading")

        postService.homePosts().enqueue(object : Callback<Post> {
            // 서버 응답있음
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when (response.code()) {
                    200, 400, 401, 500 -> {
                        Log.e("getPost", "${response.code()}")
                        callback(response.body())
                    }
                    else -> {
                        callback(response.body())
                    }
                }
            }

            // 서버 응답없음
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("getPost", "onFailure")
                callback(null)
            }

        })
    }
}