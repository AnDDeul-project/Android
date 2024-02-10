package com.umc.anddeul.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageLeaveBinding
import com.umc.anddeul.start.StartActivity

class MyPageLeaveFragment : Fragment() {
    lateinit var binding: FragmentMypageLeaveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageLeaveBinding.inflate(inflater, container, false)

        // 탈퇴하기 버튼 색상 설정
        binding.mypageLeaveReasonEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isButtonEnabled = s?.length ?: 0 >= 10
                binding.mypageLeaveBtn.isEnabled = isButtonEnabled
                binding.mypageLeaveBtn.backgroundTintList = if (isButtonEnabled) ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
                else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icon_disabled))
            }
        })

        binding.mypageLeaveBtn.setOnClickListener {
            // 탈퇴 사유 내용이 10자 이상일 때만 버튼 클릭 가능
            if (binding.mypageLeaveReasonEdit.text.length >= 10) {
                // 탈퇴 api 연결

                // 로그인 화면으로 이동
                val intent = Intent(activity, StartActivity::class.java)
                // FLAG_ACTIVITY_NO_HISTORY 플래그를 사용하여 해당 액티비티를 백 스택에서 제거
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                startActivity(intent)
            } else {
                // 10자 미만인 경우 사용자에게 메시지 표시 또는 다른 조치 수행
                Toast.makeText(requireContext(), "10자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageLeaveToolbar.apply {
            setNavigationIcon(R.drawable.mypage_setting_back)
            setNavigationOnClickListener {
                // MyPageSettingFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }
}