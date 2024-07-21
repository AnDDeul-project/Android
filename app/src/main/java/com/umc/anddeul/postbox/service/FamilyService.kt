package com.umc.anddeul.postbox.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umc.anddeul.postbox.model.FamilyResponse
import com.umc.anddeul.postbox.network.FamilyInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FamilyService {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://umc-garden.store")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val familyService = retrofit.create(FamilyInterface::class.java)

    fun getFamilyList(context: Context, accessToken: String, callback: (FamilyResponse?) -> Unit) {
        val call = familyService.familyList("Bearer $accessToken")

        call.enqueue(object : Callback<FamilyResponse> {
            override fun onResponse(call: Call<FamilyResponse>, response: Response<FamilyResponse>) {
                Log.d("Postbox FamilyService code", "${response.code()}")
                Log.d("Postbox FamilyService body", "${response.body()}")
                when (response.code()) {
                    200 -> {
                        callback(response.body())
                    }
                    401 -> {
                        val startIntent = Intent(context, StartActivity::class.java)
                        context.startActivity(startIntent)
                    }
                    500 -> {
                        callback(response.body())
                    }
                    else -> {
                        callback(null)
                    }
                }
            }

            override fun onFailure(call: Call<FamilyResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}