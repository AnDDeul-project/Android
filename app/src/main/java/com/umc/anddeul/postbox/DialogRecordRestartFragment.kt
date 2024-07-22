package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.FragmentDialogRecordRestartBinding

class DialogRecordRestartFragment(private val context: Context, private val recordPopupFragment: RecordPopupFragment) {
    private lateinit var binding: FragmentDialogRecordRestartBinding
    private val dlg = Dialog(context)

    fun show(){
        binding = FragmentDialogRecordRestartBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        // 확인 버튼
        binding.okBtn2.setOnClickListener {
            // 녹음 초기화 코드 추가
            recordPopupFragment.resetRecording()
            dlg.dismiss()
        }

        // 취소 버튼
        binding.cancelbtn2.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}