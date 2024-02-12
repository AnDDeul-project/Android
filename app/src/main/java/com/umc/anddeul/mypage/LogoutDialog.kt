package com.umc.anddeul.mypage

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.start.StartActivity

class LogoutDialog : DialogFragment() {
    lateinit var binding : FragmentDialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPermissionBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogPermissionTv.text = "로그아웃 하시겠어요?"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            // 로그아웃 api 연결

            dismiss()

            // 로그인 화면으로 이동
            val intent = Intent(activity, StartActivity::class.java)
            // FLAG_ACTIVITY_NO_HISTORY 플래그를 사용하여 해당 액티비티를 백 스택에서 제거
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            startActivity(intent)
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}