package com.umc.anddeul.start.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivitySignupBinding
import com.umc.anddeul.start.StartActivity

class SignupActivity: AppCompatActivity()  {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.signupBackBtn.setOnClickListener {
            val startIntent = Intent(this, StartActivity::class.java)
            startActivity(startIntent)
        }

        //// 기존 카카오톡으로 로그인
        binding.oldSignUpBtn.setOnClickListener {

        }

        //// 다른 카카오 계정으로 로그인
        binding.newSignUpBtn.setOnClickListener {

        }
    }
}