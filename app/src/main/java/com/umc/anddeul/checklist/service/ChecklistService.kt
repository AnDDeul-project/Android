package com.umc.anddeul.checklist.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.checklist.ChecklistRVAdapter
import com.umc.anddeul.checklist.model.AddRoot
import com.umc.anddeul.checklist.model.CheckImg
import com.umc.anddeul.checklist.model.CheckImgRoot
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.checklist.model.CompleteCheck
import com.umc.anddeul.checklist.model.CompleteRoot
import com.umc.anddeul.checklist.model.Root
import com.umc.anddeul.checklist.model.TargetImg
import com.umc.anddeul.checklist.network.ChecklistInterface
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ChecklistService(context : Context) {
    val checklistRVAdapter = ChecklistRVAdapter(context)

    //토큰 가져오기
    val spf: SharedPreferences = context!!.getSharedPreferences("myToken", Context.MODE_PRIVATE)
    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMjkzNjU1Njg2Il0sImlhdCI6MTcwNTY5ODI5OX0.9IWWuq_wRaZI_g-f8rpq4iiMrf12JhAUP2tLbsEvFCo"
//    val token = spf.getString("jwtToken", "")

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
    fun readApi(checklist: Checklist) {
        val readCall : Call<Root> = service.getChecklist(
            "3324185004",
            false,
            "2024-02-17"
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
                        checklistRVAdapter.setChecklistData(it)
                        checklistRVAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<Root>, t: Throwable) {
                Log.d("read 실패", "readCall: ${t.message}")
            }
        })
    }

    fun imgApi(checklist: Checklist, file: File) {
        val checkId = checklist.check_idx
        val checkFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        Log.d("이미지 추가", "checkId: ${checkId}, checkFile: ${checkFile}")

        val imageFileRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, imageFileRequestBody)
        val checkidRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), checkId.toString())

        Log.d("이미지 추가", "imagePart: ${imagePart}, checkidRequestBody: ${checkidRequestBody}")

        val imgCall : Call<CheckImgRoot> = service.imgPic(
            imagePart,
            checkidRequestBody
        )
        Log.d("이미지 추가", "imgCall: ${imgCall}")
        imgCall.enqueue(object : Callback<CheckImgRoot> {
            override fun onResponse(call: Call<CheckImgRoot>, response: Response<CheckImgRoot>) {
                Log.d("이미지 추가 api", "Response: ${response}")

                if (response.isSuccessful) {
                    val root : CheckImgRoot? = response.body()
                    Log.d("이미지 추가", "CheckImg Root: ${root}")
                    val checkImg : CheckImg? = root?.check
                    Log.d("이미지 추가", "CheckImg: ${checkImg}")

                    if (root?.isSuccess == true) {
                        checkImg?.let {
                            readApi(checklist)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CheckImgRoot>, t: Throwable) {
                Log.d("이미지 추가 실패", "imgCall: ${t.message}")
            }
        })
    }

    fun completeApi(checklist: Checklist) {
        val completeCall : Call<CompleteRoot> = service.complete(
            checklist.check_idx
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
                            readApi(checklist)
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