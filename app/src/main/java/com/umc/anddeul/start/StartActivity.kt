package com.umc.anddeul.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 로그인으로 이동
        binding.longinBtn.setOnClickListener {

        }

        //// 회원가입으로 이동
        binding.signupBtn.setOnClickListener {

        }
    }
}