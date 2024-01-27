package com.umc.anddeul.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.databinding.DialogConfirmBinding

class ConfirmDialog : DialogFragment() {
    lateinit var binding : DialogConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmBinding.inflate(layoutInflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogNameTv.text = "'이솜솜'님을"
        binding.dialogQuestionTv.text = "행복한 우리 가족에 추가하시겠습니까?"

        // 취소 버튼
        binding.dialogCancelBtn.setOnClickListener {
            dismiss()
        }

        // 수락하기 버튼
        binding.dialogAcceptBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}