package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.MainActivity
import com.umc.anddeul.databinding.FragmentDialogPostSendBinding

class DialogPostSendFragment(private val context: Context) {
    private lateinit var binding: FragmentDialogPostSendBinding
    private val dlg = Dialog(context)

    fun show(){
        binding = FragmentDialogPostSendBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        // 확인 버튼 (녹음 삭제)
        binding.okBtn6.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}