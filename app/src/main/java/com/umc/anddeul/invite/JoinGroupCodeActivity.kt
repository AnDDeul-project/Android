package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.databinding.ActivityJoinGroupCodeBinding
import com.umc.anddeul.invite.model.FamilyImage
import com.umc.anddeul.invite.service.FamilyAddService
import com.umc.anddeul.invite.service.FamilyInfoService

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
                } else {
                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                }
            } else {
                AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
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
                    when (inviteDto.status) {
                        200 -> {
                            val sendIntent = Intent(this, JoinGroupSendActivity::class.java)
                            startActivity(sendIntent)
                        }
                        409 -> {
                            AnddeulErrorToast.createToast(this, "이미 가족이 존재합니다.")?.show()
                        }
                        else -> {
                            AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
                        }
                    }
                } else {
                    AnddeulErrorToast.createToast(this, "서버 연결이 불안정합니다.")?.show()
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