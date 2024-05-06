package com.umc.anddeul.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.ActivityStartBinding
import com.umc.anddeul.invite.JoinGroupSendActivity
import com.umc.anddeul.start.service.RequestService
import com.umc.anddeul.start.signin.LoginActivity
import com.umc.anddeul.start.signin.SignupActivity

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    companion object {
        lateinit var _startActivity: StartActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _startActivity = this

        binding = ActivityStartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// api 연결
        if (!loadJwt().isNullOrEmpty()){    // 토큰이 있을 때 (로그인이 되어있을 때)
            val requestService = RequestService()
            requestService.requestInfo(loadJwt()) { requestDTO ->
                if (requestDTO != null) {
                    if (requestDTO.isSuccess.toString() == "true") {
                        if (requestDTO.infamily){   // 가족이 있을 때
                            val mainIntent = Intent(this, MainActivity::class.java)
                            startActivity(mainIntent)
                        }
                        else if (requestDTO.request){   // 가족이 없고 요청이 있을 때
                            val joinIntent = Intent(this, JoinGroupSendActivity::class.java)
                            startActivity(joinIntent)
                        }
                    }
                }
            }
        }

        //// 로그인으로 이동
        binding.longinBtn.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        //// 회원가입으로 이동
        binding.signupBtn.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    override fun onBackPressed() {
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }

}