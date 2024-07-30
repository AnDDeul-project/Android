package com.umc.anddeul.invite

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.databinding.FragmentDialogNotExistCodeBinding

class DialogNotExistCodeFragment(private val context: Context) {
    private lateinit var binding: FragmentDialogNotExistCodeBinding
    private val dlg = Dialog(context)

    fun show(){
        binding = FragmentDialogNotExistCodeBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        // 다시 입력하기 버튼
        binding.okBtn4.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}