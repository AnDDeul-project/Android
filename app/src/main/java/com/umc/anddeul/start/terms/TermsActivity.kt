package com.umc.anddeul.start.terms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityTermsBinding

class TermsActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 동의 완료 버튼
        binding.termsAgreeBtn.setOnClickListener {

        }

    }
}