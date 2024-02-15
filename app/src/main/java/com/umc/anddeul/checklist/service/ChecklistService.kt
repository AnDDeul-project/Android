package com.umc.anddeul.checklist.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.umc.anddeul.checklist.ChecklistRVAdapter
import com.umc.anddeul.checklist.model.AddRoot
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.CompleteCheck
import com.umc.anddeul.checklist.model.CompleteRoot
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
//    var checklistRVAdapter : ChecklistRVAdapter = ChecklistRVAdapter(context)

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
    fun readApi() {
        val readCall : Call<Root> = service.getChecklist(
            "3304133093",
            false,
            "2024-02-12"
        )
        Log.d("조회", "readCall ${readCall}")
        readCall.enqueue(object : Callback<Root> {
            override fun onResponse(call: Call<Root>, response: Response<Root>) {
                Log.d("api 조회", "Response ${response}")

                if (response.isSuccessful) {
                    val root : Root? = response.body()
                    Log.d("조회", "Root : ${root}")
                    val result : List<Checklist>? = root?.checklist
                    Log.d("조회", "Result : ${result}")

                    result?.let {
//                        checklistRVAdapter.setChecklistData(it)
//                        checklistRVAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read 실패", "readCall: ${t.message}")
            }
        })
    }

    fun completeApi() {
        val completeCall : Call<CompleteRoot> = service.complete(
            17
        )
        Log.d("완료", "completeCall : ${completeCall}")
        completeCall.enqueue(object : Callback<CompleteRoot> {
            override fun onResponse(call: Call<CompleteRoot>, response: Response<CompleteRoot>) {
                Log.d("api 완료 변경", "Response ${response}")

                if (response.isSuccessful) {
                    val root : CompleteRoot? = response.body()
                    Log.d("완료", "Complete Root : ${root}")
                    val check : CompleteCheck? = root?.check
                    Log.d("완료", "Check: ${check}")

                    if (root?.isSuccess == true) {
                        check.let {
                            readApi()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<CompleteRoot>, t: Throwable) {
                Log.d("complete 실패", "completeCall : ${t.message}")
            }
        })
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