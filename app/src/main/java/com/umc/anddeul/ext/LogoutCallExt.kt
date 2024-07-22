package com.umc.anddeul.ext

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.util.Log
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.mypage.model.LogoutDTO
import com.umc.anddeul.mypage.network.LogoutInterface
import com.umc.anddeul.start.StartActivity

fun <T> Call<T>.enqueueWithLogoutOnUnauthorized(activity: Context, callback: Callback<T>) {
    this.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.code() == 401) {
                // 로그아웃 API 호출
                val retrofit = RetrofitManager.getRetrofitInstanceWithoutAuth()
                val logoutService = retrofit.create(LogoutInterface::class.java)
                logoutService.logout().enqueue(object : Callback<LogoutDTO> {
                    override fun onResponse(call: Call<LogoutDTO>, response: Response<LogoutDTO>) {
                        if (response.isSuccessful) {
                            // 토큰 초기화
                            TokenManager.clearToken()

                            // 로그인 화면으로 이동
                            val intent = Intent(activity, StartActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            activity.startActivity(intent)
                        } else {
                            Log.e("logoutService", "로그아웃 실패")
                        }
                    }

                    override fun onFailure(call: Call<LogoutDTO>, t: Throwable) {
                        Log.e("logoutService", "로그아웃 호출 실패: ${t.message}")
                    }
                })
            } else {
                callback.onResponse(call, response)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            callback.onFailure(call, t)
        }
    })
}
