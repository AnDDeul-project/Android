package com.umc.anddeul.start.setprofile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivitySetprofileBinding
import com.umc.anddeul.start.terms.TermsActivity

class SetProfileActivity: AppCompatActivity()  {
    private lateinit var binding: ActivitySetprofileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetprofileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 뒤로 가기
        binding.setProfileBackBtn.setOnClickListener {
            val termsIntent = Intent(this, TermsActivity::class.java)
            startActivity(termsIntent)
        }

    }
}