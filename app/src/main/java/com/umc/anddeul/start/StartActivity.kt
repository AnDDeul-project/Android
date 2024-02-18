package com.umc.anddeul.start

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityStartBinding
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

}