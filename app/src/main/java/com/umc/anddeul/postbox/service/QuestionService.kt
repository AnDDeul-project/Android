package com.umc.anddeul.postbox.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umc.anddeul.postbox.model.QuestionResponse
import com.umc.anddeul.postbox.network.QuestionInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val questionService = retrofit.create(QuestionInterface::class.java)

    fun randomQuestion(context: Context, accessToken: String, callback: (QuestionResponse?) -> Unit) {
        val call = questionService.questionRandom("Bearer $accessToken")

        call.enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(call: Call<QuestionResponse>, response: Response<QuestionResponse>) {
                Log.d("Postbox QuestionService code", "${response.code()}")
                Log.d("Postbox QuestionService body", "${response.body()}")
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        val startIntent = Intent(context, StartActivity::class.java)
                        context.startActivity(startIntent)
                    }
                    405 -> {
                        callback(response.body())
                    }
                    500 -> {
                        callback(response.body())
                    }
                    4001 -> {
                        callback(response.body())
                    }
                    else -> {
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}