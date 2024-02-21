package com.umc.anddeul.invite

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.MainActivity
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.databinding.ActivityCreateGroupCodeBinding

class CreateGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 그룹 생성 세팅
        binding.groupCode.text = intent.getStringExtra("FAMILY_GROUP_CODE")
        binding.groupExplainTv.text = "\'${intent.getStringExtra("FAMILY_GROUP_NAME")}\'"

        //// 카카오톡으로 전송하기
        binding.sendCodeBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            val groupCodeText = intent.getStringExtra("FAMILY_GROUP_CODE")
            val content = "가족이 초대링크를 공유했어요!"
            intent.putExtra(Intent.EXTRA_TEXT,"$content\n\n$groupCodeText")
            val chooserTitle = "가족에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))

        }
        
        //// 복사하기
        binding.copyCodeBtn.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("가족코드", binding.groupCode.text.toString())
            clipboard.setPrimaryClip(clip)
            // 복사 완료 메시지
            AnddeulToast.createToast(this, "가족 코드가 복사되었습니다")?.show()
        }
        
        //// 메인 페이지로 이동
        binding.gotoStartBtn.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

    }
}