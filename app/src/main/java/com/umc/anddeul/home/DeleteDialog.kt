package com.umc.anddeul.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.home.model.PostDelete
import com.umc.anddeul.home.network.PostDeleteInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeleteDialog(val postId : Int) : DialogFragment() {
    lateinit var binding: FragmentDialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPermissionBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogPermissionTv.text = "게시물을 삭제 하시겠어요?"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            delete(postId)
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun delete(postId : Int) {
        val spf: SharedPreferences =
            requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzI0MTg1MDA0Il0sImlhdCI6MTcwODE0OTYzN30.gdMMpNYi6ewkV8ND2vsU138Z9nryiXQNfr-HvUnQUL8"

        val retrofitBearer = Retrofit.Builder()
            .baseUrl("http://umc-garden.store")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.orEmpty())
                            .build()
                        Log.d("retrofitBearer", "Token: ${token.toString()}" + token.orEmpty())
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        val deleteService = retrofitBearer.create(PostDeleteInterface::class.java)

        deleteService.deletePost(postId).enqueue(object : Callback<PostDelete> {
            override fun onResponse(call: Call<PostDelete>, response: Response<PostDelete>) {
                Log.e("deleteService", "onResponse")
                Log.e("deleteService response code : ", "${response.code()}")
                Log.e("deleteService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    // 게시글 삭제 응답 코드가 200이라면 다이얼로그를 닫고 HomeFragment의 loadPost 함수 호출
                    if (deleteResponse!!.code == 2000) {
                        dismiss()

                        // 홈 프래그먼트의 loadPost 함수 호출해서 게시글 새로고침
                        val homeFragment = parentFragment as? HomeFragment
                        homeFragment?.loadPost()
                    }
                }

            }

            override fun onFailure(call: Call<PostDelete>, t: Throwable) {
                Log.e("deleteService", "onFailure")
                Log.e("deleteService", "Failure message: ${t.message}")
            }

        })
    }
}