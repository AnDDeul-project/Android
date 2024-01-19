package com.umc.anddeul.start.setprofile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivitySetprofileBinding

class SetProfileActivity: AppCompatActivity()  {
    private lateinit var binding: ActivitySetprofileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetprofileBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }
}