package com.umc.anddeul.mypage

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageNotificationBinding

class MyPageNotificationFragment : Fragment() {
    lateinit var binding : FragmentMypageNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageNotificationBinding.inflate(inflater, container, false)

        binding.mypageNotificationBtn.setOnClickListener {
            openNotificationSettings()
        }
        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageNotificationBackIv.setOnClickListener {
            // MyPageSettingFragment로 이동
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    fun openNotificationSettings() {
        val intent = Intent()

        // 알림 설정 화면으로 이동
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS

        // 패키지 이름 및 알림 채널 설정을 위한 인텐트 데이터 추가
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)

        // 패키지 매니저를 통해 현재 앱의 정보를 가져와서 UID를 설정
        val packageManager: PackageManager = requireContext().packageManager
        val applicationInfo = packageManager.getApplicationInfo(requireActivity().packageName, 0)
        intent.putExtra("app_uid", applicationInfo.uid)

        startActivity(intent)
    }

}