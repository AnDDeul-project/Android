package com.umc.anddeul.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.MainActivity
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.ActivitySplashBinding
import com.umc.anddeul.invite.JoinGroupSendActivity
import com.umc.anddeul.start.service.RequestService
import com.umc.anddeul.start.service.TokenService

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            initViews()
        }, 1000)

    }

    private fun initViews() {

        val jwt = loadJwt()

        // 자동 로그인 구현
        if (!jwt.isNullOrEmpty()){    // 토큰이 비어있지 않을 때
            //// api 연결 - 토큰 유효한지 판별
            val tokensService = TokenService()
            tokensService.requestToken(jwt) { tokenDTO ->
                if (tokenDTO != null && tokenDTO.isSuccess.toString() == "true") {
                    //// api 연결 - 가족 or 요청 있는지 판별
                    val requestService = RequestService()
                    requestService.requestInfo(jwt) { requestDTO ->
                        if (requestDTO != null) {
                            if (requestDTO.isSuccess.toString() == "true") {
                                if (requestDTO.infamily) {   // 가족이 있을 때
                                    val mainIntent = Intent(this, MainActivity::class.java)
                                    startActivity(mainIntent)
                                } else if (requestDTO.request) {   // 가족이 없고 요청이 있을 때
                                    val joinIntent = Intent(this, JoinGroupSendActivity::class.java)
                                    startActivity(joinIntent)
                                } else {
                                    gotoStart()
                                }
                            } else {
                                gotoStart()
                            }
                        } else {
                            gotoStart()
                        }
                    }
                } else {
                    gotoStart()
                }
            }
        }else {
            gotoStart()
        }
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }

    private fun gotoStart(){
        // 유효 토큰 없음 (자동 로그인 안됨)
        val startIntent = Intent(this, StartActivity::class.java)
        startActivity(startIntent)
    }
}