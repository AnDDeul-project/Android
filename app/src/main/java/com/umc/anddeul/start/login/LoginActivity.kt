package com.umc.anddeul.start.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 기존 카카오톡으로 로그인
        binding.oldLoginBtn.setOnClickListener {

        }

        //// 다른 카카오 계정으로 로그인
        binding.newLoginBtn.setOnClickListener {

        }
    }
}