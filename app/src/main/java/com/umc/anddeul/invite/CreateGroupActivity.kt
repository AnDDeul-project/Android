package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.umc.anddeul.databinding.ActivityCreateGroupBinding
import com.umc.anddeul.invite.service.FamilyNewService

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGroupBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 새로운 그룹 만들기
        binding.groupNmBtn.setOnClickListener {
            if(binding.groupNm.text.toString().isNotBlank()) {
                // api 연결
                val loadedToken = loadJwt() // jwt토큰
                val familyNewService = FamilyNewService()
                familyNewService.createFamily(loadedToken, binding.groupNm.text.toString()) { inviteDto ->
                    if (inviteDto != null) {
                        if (inviteDto.isSuccess.toString() == "true") {
                            val codeIntent = Intent(this, CreateGroupCodeActivity::class.java)
                            codeIntent.putExtra("FAMILY_GROUP_NAME", binding.groupNm.text.toString())
                            codeIntent.putExtra("FAMILY_GROUP_CODE", inviteDto.randomToken[0])
                            startActivity(codeIntent)
                        }
                    } else {
                    }
                }
            }
        }

    }

    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }
}