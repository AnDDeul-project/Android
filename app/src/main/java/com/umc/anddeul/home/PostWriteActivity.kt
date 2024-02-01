package com.umc.anddeul.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ActivityPostWriteBinding

@Suppress("DEPRECATION")
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
        val selectedImagesUri : ArrayList<Uri> = intent.getParcelableArrayListExtra("selectedImages")!!

        val selectedVPAdapter = SelectedVPAdapter(selectedImagesUri)
        binding.uploadWriteSelectedVp.adapter = selectedVPAdapter
        binding.uploadWriteSelectedVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // 받아온 이미지 URI 목록을 이용하여 이미지를 나열
        val copiedImagesUri: List<Uri> = ArrayList(selectedImagesUri)
        for (imageUri in copiedImagesUri) {
            selectedVPAdapter.addImage(imageUri)
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