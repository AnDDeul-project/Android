package com.umc.anddeul.invite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityJoinGroupSendBinding

class JoinGroupSendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupSendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupSendBinding.inflate(layoutInflater)

        setContentView(binding.root)
        

    }
}