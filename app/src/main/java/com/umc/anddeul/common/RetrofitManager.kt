package com.umc.anddeul.common

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private lateinit var retrofitBearer: Retrofit
    private lateinit var retrofitNoAuth: Retrofit
    var token: String? = null

    fun initialize(baseUrl: String) {
        token = TokenManager.getToken()

        retrofitBearer = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.orEmpty())
                            .build()
                        Log.d("retrofitBearer", "Token: ${token.toString()}")
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        retrofitNoAuth = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build()) // 토큰 없이 기본 OkHttpClient 사용
            .build()
    }

    fun getRetrofitInstance(): Retrofit {
        if (!::retrofitBearer.isInitialized) {
            throw UninitializedPropertyAccessException("RetrofitManager must be initialized")
        }
        return retrofitBearer
    }

    fun getRetrofitInstanceWithoutAuth(): Retrofit {
        if (!::retrofitNoAuth.isInitialized) {
            throw UninitializedPropertyAccessException("RetrofitManager must be initialized")
        }
        return retrofitNoAuth
    }
}

