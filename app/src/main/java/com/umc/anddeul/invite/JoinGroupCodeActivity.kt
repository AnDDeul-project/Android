package com.umc.anddeul.invite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityJoinGroupCodeBinding

class JoinGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 가족 그룹에 참여하기
        binding.existGroupJoinBtn.setOnClickListener {
        }

    }
}