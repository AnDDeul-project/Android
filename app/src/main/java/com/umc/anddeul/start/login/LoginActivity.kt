package com.umc.anddeul.start.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityLoginBinding
import com.umc.anddeul.start.StartActivity

class LoginActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.loginBackBtn.setOnClickListener {
            val startIntent = Intent(this, StartActivity::class.java)
            startActivity(startIntent)
        }

        //// 기존 카카오톡으로 로그인
        binding.oldLoginBtn.setOnClickListener {

        }

        //// 다른 카카오 계정으로 로그인
        binding.newLoginBtn.setOnClickListener {

        }
    }
}