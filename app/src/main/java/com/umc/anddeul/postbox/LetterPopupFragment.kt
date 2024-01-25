package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.umc.anddeul.databinding.FragmentPopupLetterBinding

class LetterPopupFragment(private val context: Context)  {
    private lateinit var binding : FragmentPopupLetterBinding
    private val dlg = Dialog(context)

    fun show(content: Letter) {
        binding = FragmentPopupLetterBinding.inflate(LayoutInflater.from(context))
        binding.familyTv.text = content.name

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        dlg.show()
    }
}