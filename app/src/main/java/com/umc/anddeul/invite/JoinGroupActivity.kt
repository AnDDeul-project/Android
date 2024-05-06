package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.umc.anddeul.databinding.ActivityJoinGroupBinding
import com.umc.anddeul.invite.service.FamilyInfoService

class JoinGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 기존 그룹에 참여하기
        binding.existGroupNmBtn.setOnClickListener {
            if (binding.existGroupNm.text.isNotBlank()) {

                // api 연결
                val loadedToken = loadJwt() // jwt토큰
                val familyInfoService = FamilyInfoService()
                familyInfoService.searchFamily(loadedToken, binding.existGroupNm.text.toString()) { inviteDto ->
                    if (inviteDto != null) {
                        if (inviteDto.isSuccess.toString() == "true") { // 그룹코드 존재
                            val codeIntent = Intent(this, JoinGroupCodeActivity::class.java)
                            codeIntent.putExtra("GROUP_CODE", binding.existGroupNm.text.toString())
                            startActivity(codeIntent)
                        }
                    } else {    // 그룹코드 존재x
                        val notExistCodeFragment = DialogNotExistCodeFragment(this)
                        notExistCodeFragment.show()
                    }
                }

            }
        }

        //// 뒤로 가기
        binding.createGoBackBtn.setOnClickListener {
            val startIntent = Intent(this, InviteStartActivity::class.java)
            startActivity(startIntent)
        }
    }


    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }
}