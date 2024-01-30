package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityJoinGroupCodeBinding
import com.umc.anddeul.start.StartActivity

class JoinGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 가족 그룹에 참여하기
        binding.existGroupJoinBtn.setOnClickListener {
            val sendIntent = Intent(this, JoinGroupSendActivity::class.java)
            startActivity(sendIntent)
        }

        //// 뒤로가기
        binding.codeBackBtn.setOnClickListener {
            val joinIntent = Intent(this, JoinGroupActivity::class.java)
            startActivity(joinIntent)
        }

    }
}