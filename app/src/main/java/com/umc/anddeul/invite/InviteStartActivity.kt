package com.umc.anddeul.invite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityInviteStartBinding

class InviteStartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInviteStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInviteStartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 새로운 그룹 만들기
        binding.newGroupBtn.setOnClickListener {
        }

        //// 기존 그룹에 참여하기
        binding.participateGroupBtn.setOnClickListener {

        }

    }
}