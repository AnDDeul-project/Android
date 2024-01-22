package com.umc.anddeul.invite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityCreateGroupCodeBinding

class CreateGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 카카오톡으로 전송하기
        binding.sendCodeBtn.setOnClickListener {
        }

        //// 복사하기
        binding.copyCodeBtn.setOnClickListener {
        }

    }
}