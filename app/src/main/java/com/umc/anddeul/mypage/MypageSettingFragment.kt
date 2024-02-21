package com.umc.anddeul.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageSettingBinding
import com.umc.anddeul.home.PermissionDialog

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
            // 알림 설정 화면으로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
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
            // 탈퇴하기 화면으로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_setting_layout, MyPageLeaveFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        setToolbar()

        return binding.root
    }

    // 툴바 셋팅
    fun setToolbar() {
        binding.mypageSettingToolbar.apply {
            setNavigationIcon(R.drawable.mypage_setting_back)

            setNavigationOnClickListener {
                // MyPageFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }
}