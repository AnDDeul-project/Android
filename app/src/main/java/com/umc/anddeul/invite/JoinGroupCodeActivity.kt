package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.ActivityJoinGroupCodeBinding
import com.umc.anddeul.invite.model.FamilyImage
import com.umc.anddeul.invite.service.FamilyAddService
import com.umc.anddeul.invite.service.FamilyInfoService
import com.umc.anddeul.start.terms.TermsActivity

class JoinGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupCodeBinding
    private lateinit var profileAdapter: FamilyProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 그룹에 속한 가족 리스트
        profileAdapter = FamilyProfileAdapter()

        // api 연결

        val groupCode = intent.getStringExtra("GROUP_CODE") // 가족 코드
        val loadedToken = loadJwt() // jwt토큰
        val familyInfoService = FamilyInfoService()
        familyInfoService.searchFamily(loadedToken, groupCode.toString()) { inviteDto ->
            if (inviteDto != null) {
                if (inviteDto.isSuccess.toString() == "true") {
                    profileAdapter.families = inviteDto.familyImages.map { FamilyImage(it.image) }
                    profileAdapter.notifyDataSetChanged()
                    binding.GroupNameTv.text = inviteDto.familyName
                    binding.familyCntTv.text = "${inviteDto.familyCount}명"
                    binding.inviteCodeTv.text = "초대코드 ${groupCode.toString()}"
                }
            } else {
            }
        }

        binding.rvFamiles.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvFamiles.adapter = profileAdapter

        val leftSpace = FamilyProfileAdapter.DimensionUtils.dpToPx(this, 13f)
        binding.rvFamiles.addItemDecoration(FamilyProfileAdapter.HorizontalSpaceDecoration(leftSpace))


        //// 가족 그룹에 참여하기
        binding.existGroupJoinBtn.setOnClickListener {

            // api 연결
            val familyAddService = FamilyAddService()
            familyAddService.addFamily(loadedToken, groupCode.toString()) { inviteDto ->
                if (inviteDto != null) {
                    if (inviteDto.isSuccess.toString() == "true") {
                        val sendIntent = Intent(this, JoinGroupSendActivity::class.java)
                        startActivity(sendIntent)
                    }
                } else {
                }
            }
        }

        //// 뒤로가기
        binding.codeBackBtn.setOnClickListener {
            val joinIntent = Intent(this, JoinGroupActivity::class.java)
            startActivity(joinIntent)
        }

    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }
}