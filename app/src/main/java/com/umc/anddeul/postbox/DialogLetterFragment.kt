package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.databinding.FragmentDialogLetterBinding

class DialogLetterFragment(private val context: Context) {
    private lateinit var binding: FragmentDialogLetterBinding
    private val dlg = Dialog(context)

    fun show(postType: String){
        binding = FragmentDialogLetterBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        // 녹음 경고
        if(postType == "voice"){
            binding.titleTv.text = "이미 편지를 적은 경우\n녹음할 수 없습니다."
            binding.contentTv.text = "녹음 편지를 작성하고 싶으시다면,\n내용을 지워주세요"
        }

        // 확인 버튼
        binding.okBtn3.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}