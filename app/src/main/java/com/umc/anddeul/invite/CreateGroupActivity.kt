package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 새로운 그룹 만들기
        binding.groupNmBtn.setOnClickListener {
            if(binding.groupNm.text.toString().isNotBlank()) {
                val codeIntent = Intent(this, CreateGroupCodeActivity::class.java)
                startActivity(codeIntent)
            }
        }

    }
}