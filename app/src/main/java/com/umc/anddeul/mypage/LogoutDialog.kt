package com.umc.anddeul.mypage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.home.model.LogoutDTO
import com.umc.anddeul.home.network.LogoutInterface
import com.umc.anddeul.start.StartActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LogoutDialog : DialogFragment() {
    lateinit var binding : FragmentDialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPermissionBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogPermissionTv.text = "로그아웃 하시겠어요?"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            // 로그아웃 api 연결
            myPageLogout()
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun myPageLogout() {
        val spf: SharedPreferences = requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNzc1MjQ1OH0.gv84EPPvswVZnhSp6KAaNSGCx6oDoYXR37e46cGxvvo"

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

        val logoutService = retrofitBearer.create(LogoutInterface::class.java)

        logoutService.logout().enqueue(object : Callback<LogoutDTO> {
            override fun onResponse(call: Call<LogoutDTO>, response: Response<LogoutDTO>) {
                Log.e("logoutService", "onResponse")
                Log.e("logoutService", "${response.code()}")
                Log.e("logoutService", "${response.body()}")

                if (response.isSuccessful) {
                    dismiss()

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, StartActivity::class.java)
                    // FLAG_ACTIVITY_NO_HISTORY 플래그를 사용하여 해당 액티비티를 백 스택에서 제거
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()

                    startActivity(intent)
                } else {
                    Log.e("logoutService", "로그아웃 실패")
                }
            }

            override fun onFailure(call: Call<LogoutDTO>, t: Throwable) {
                Log.e("logoutService", "onFailure")
                Log.e("logoutService", "Failure message: ${t.message}")
            }
        })
    }
}