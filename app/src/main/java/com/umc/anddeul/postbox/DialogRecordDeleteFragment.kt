package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.FragmentDialogRecordDeleteBinding

class DialogRecordDeleteFragment(private val context: Context, private val postBoxFragment: PostboxFragment) {
    private lateinit var binding: FragmentDialogRecordDeleteBinding
    private val dlg = Dialog(context)

    fun show(){
        binding = FragmentDialogRecordDeleteBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        // 확인 버튼 (녹음 삭제)
        binding.okBtn4.setOnClickListener {
            postBoxFragment.deleteRecording()
            dlg.dismiss()
        }

        // 취소 버튼 (녹음 삭제 취소)
        binding.cancelbtn4.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}