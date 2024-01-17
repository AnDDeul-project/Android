package com.umc.anddeul.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.databinding.FragmentHomeBinding

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

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        // swipe refresh layout 초기화 (swipe 해서 피드 새로고침)
        binding.homeSwipeRefresh.setOnRefreshListener {

        }

        // 햄버거 버튼 클릭 이벤트 처리
        binding.homeToolbarMenuIb.setOnClickListener {

        }

        // Floating Action Button 클릭 시
        binding.homeFloatingBt.setOnClickListener {

        }

        return binding.root
    }

    fun floatingButtonSetting() {

    }
}