package com.umc.anddeul.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageBinding

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 게시글 더미
        val myFeed = listOf("1", "2", "3", "4", "5")

        val mypageProfileRVAdapter = MyPageProfileRVAdapter(myFeed)
        binding.mypageProfileRv.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.mypageProfileRv.adapter = mypageProfileRVAdapter

        binding.mypageSettingIb.setOnClickListener {
            // MyPageSettingFragment로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_layout, MypageSettingFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.mypageModifyBtn.setOnClickListener {
            // MyPageModify로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_layout, MyPageModify())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}