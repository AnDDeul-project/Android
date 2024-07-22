package com.umc.anddeul.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding

class PermissionDialog : DialogFragment(){
    lateinit var binding: FragmentDialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPermissionBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogPermissionTv.text = "사진을 선택 하시려면 권한이 필요합니다"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            // 확인 버튼 클릭 시 앱 설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}

