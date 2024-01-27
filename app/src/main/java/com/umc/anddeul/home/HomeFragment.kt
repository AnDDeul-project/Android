package com.umc.anddeul.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.databinding.FragmentHomeBinding
import com.umc.anddeul.databinding.FragmentHomeMenuMemberBinding
import com.umc.anddeul.databinding.FragmentHomeMenuRequestMemberBinding

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    private var postDatas = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        postDatas.apply { // 임시 데이터 더미
            add(Post(1, "user1", "안녕하세요 첫번째 피드글입니다", "사진", 1, 1))
            add(Post(2, "user2", "안녕하세요 두번째 피드글입니다", "사진", 2, 2))
        }

        val postRVAdapter = PostRVAdapter(requireContext(), postDatas) // 어댑터와 postDatas 연결
        binding.homeFeedRv.adapter = postRVAdapter // recyclerView에 Adapter 연결
        binding.homeFeedRv.layoutManager = LinearLayoutManager(context)

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout : DrawerLayout = binding.homeDrawerLayout

        binding.homeToolbarMenuIb.setOnClickListener {
            Log.e("toolbar", "click!!!!!!!!")
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END)
            } else {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        val memberLayout = binding.homeMenuMembersLinear

        // 더미 데이터 리스트
        val memberDataList = listOf("이솜솜", "김솜솜", "박솜솜", "최솜솜")

        for (memberData in memberDataList) {
            val memberBinding = FragmentHomeMenuMemberBinding.inflate(LayoutInflater.from(context), memberLayout, true)
            memberBinding.homeMenuMemberNameTv.text = memberData
        }

        val requestMemberLayout = binding.homeMenuRequestMembersLinear
        // 수락 요청 더미 데이터 리스트
        val requestMemberDataList = listOf("이솜솜", "김솜솜")
        for (requestMember in requestMemberDataList) {
            val memberBinding = FragmentHomeMenuRequestMemberBinding.inflate(LayoutInflater.from(context), requestMemberLayout, true)
            memberBinding.homeMenuRequestMemberNameTv.text = requestMember

            memberBinding.homeMenuRequestAcceptBt.setOnClickListener {
                val dialog = ConfirmDialog()
                dialog.isCancelable = false
                dialog.show(parentFragmentManager, "confirm dialog")
            }
        }



        // swipe refresh layout 초기화 (swipe 해서 피드 새로고침)
        binding.homeSwipeRefresh.setOnRefreshListener {

        }

        // Floating Action Button 클릭 시
        binding.homeFloatingBt.setOnClickListener {

        }

        return binding.root
    }

    fun floatingButtonSetting() {

    }
}