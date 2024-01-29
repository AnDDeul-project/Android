package com.umc.anddeul.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ActivityPostWriteBinding

class PostWriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostWriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadWriteToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // 현재 Activity를 종료
                finish()
            }
        }

        // 이미지 URI 목록 받아오기
        val selectedImagesUri = intent.getStringArrayListExtra("selectedImages")

        // 받아온 이미지 URI 목록을 이용하여 이미지를 나열
        if (selectedImagesUri != null) {
            for (imageUriString in selectedImagesUri) {
                val imageUri = Uri.parse(imageUriString)

                // 예시로 ImageView를 사용하여 이미지를 나열
                // val imageView = ImageView(this)

                binding.uploadWriteSelectedIv.setImageURI(imageUri)
            }
        }

        binding.uploadWriteBtn.setOnClickListener {
            // 서버에 데이터 전송
            // homeFragment로 돌아가기
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }.also {
                startActivity(it)
            }
            finishAffinity()
        }

    }


}