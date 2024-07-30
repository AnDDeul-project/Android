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

        // 알림 설정
        binding.mypageSettingNotification.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.mypage_setting_layout, MyPageNotificationFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 로그아웃
        binding.mypageSettingLogout.setOnClickListener {
            val logoutDialog = LogoutDialog()
            logoutDialog.isCancelable = false
            logoutDialog.show(parentFragmentManager, "logout dialog")
        }

        // 탈퇴하기
        binding.mypageSettingLeave.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.mypage_setting_layout, MyPageLeaveFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 약관 확인
        binding.mypageSettingTerms.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.mypage_setting_layout, MyPageTermsFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        setToolbar()

        return binding.root
    }

    // 툴바 셋팅
    fun setToolbar() {
        binding.mypageSettingBackIv.setOnClickListener {
            // MyPageFragment로 이동
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }
}