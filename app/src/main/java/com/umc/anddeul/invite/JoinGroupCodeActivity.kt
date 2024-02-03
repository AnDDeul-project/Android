package com.umc.anddeul.invite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ActivityJoinGroupCodeBinding
import com.umc.anddeul.postbox.Letter

class JoinGroupCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinGroupCodeBinding
    private lateinit var profileAdapter: FamilyProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupCodeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //// 그룹에 속한 가족 리스트
        profileAdapter = FamilyProfileAdapter()

        // 테스트용 더미 데이터
        val dummyDatas = listOf(
            Family("http://k.kakaocdn.net/dn/xxGa9/btsB5AD9Zx0/GezVLYpUOwIdOpSlU7dYo1/img_640x640.jpg"),
            Family("http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg"),
            Family("http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg"),
            Family("http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg"),
        )

        profileAdapter.families = dummyDatas

        binding.rvFamiles.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvFamiles.adapter = profileAdapter

        val leftSpace = FamilyProfileAdapter.DimensionUtils.dpToPx(this, 13f)
        binding.rvFamiles.addItemDecoration(FamilyProfileAdapter.HorizontalSpaceDecoration(leftSpace))


        //// 가족 그룹에 참여하기
        binding.existGroupJoinBtn.setOnClickListener {
            val sendIntent = Intent(this, JoinGroupSendActivity::class.java)
            startActivity(sendIntent)
        }

        //// 뒤로가기
        binding.codeBackBtn.setOnClickListener {
            val joinIntent = Intent(this, JoinGroupActivity::class.java)
            startActivity(joinIntent)
        }

    }
}