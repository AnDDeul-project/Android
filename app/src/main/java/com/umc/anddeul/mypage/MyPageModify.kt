package com.umc.anddeul.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageModifyProfileBinding

class MyPageModify : Fragment() {
    lateinit var binding: FragmentMypageModifyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageModifyProfileBinding.inflate(inflater, container, false)

        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageModifyProfileToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // MyPageFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }
}