package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityJoinGroupBinding

class JoinGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 기존 그룹에 참여하기
        binding.existGroupNmBtn.setOnClickListener {
            val codeIntent = Intent(this, JoinGroupCodeActivity::class.java)
            startActivity(codeIntent)
        }

    }
}