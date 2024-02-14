package com.umc.anddeul.checklist.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.umc.anddeul.checklist.model.AddRoot
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.network.ChecklistInterface
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChecklistService(context : Context) {

    //토큰 가져오기
    val spf: SharedPreferences = context!!.getSharedPreferences("myToken", Context.MODE_PRIVATE)
    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMjkzNjU1Njg2Il0sImlhdCI6MTcwNTY5ODI5OX0.9IWWuq_wRaZI_g-f8rpq4iiMrf12JhAUP2tLbsEvFCo"

    val retrofit = Retrofit.Builder()
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

    val service = retrofit.create(ChecklistInterface::class.java)
    fun readCheck() {
        val readCallback = object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                if (response.isSuccessful) {
                    val root: Root? = response.body()
//                    checklistRVAdapter.list = root?.result
//                    checklistRVAdapter.notifyDataSetChanged()
                } else {
                    Log.d("apiCallback", "response unsuccessful")
                    Log.d("apiCallback", response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read apiCallback", "API CALL Failure ${t.message}")
            }
        }

        val readCall: Call<Root> = service.getChecklist(
            "userid",
            false,
            "2000-01-01"
        )

        readCall.enqueue(readCallback)
    }

    fun addCheck() {
        val addCallback = object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                if (response.isSuccessful) {
                    callbackFlow<AddRoot> {
                        response.body()
                    }
                }
                else {
                    Log.d("apiCallback", "response unsuccessful")
                    Log.d("apiCallback", response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("add apiCallback", "API CALL Failure ${t.message}")
            }
        }
    }

}