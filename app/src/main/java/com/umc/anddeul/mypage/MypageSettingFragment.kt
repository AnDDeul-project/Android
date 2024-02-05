package com.umc.anddeul.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageSettingBinding

class MypageSettingFragment : Fragment(){
    lateinit var binding: FragmentMypageSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageSettingBinding.inflate(inflater, container, false)

        setToolbar()

        return binding.root
    }

    // 툴바 셋팅
    fun setToolbar() {
        binding.mypageSettingToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // MyPageFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }
}