package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.umc.anddeul.databinding.FragmentPopupLetterBinding

class LetterPopupFragment(private val context: Context)  {
    private lateinit var binding : FragmentPopupLetterBinding
    private val dlg = Dialog(context)

    fun show(content: Letter) {
        binding = FragmentPopupLetterBinding.inflate(LayoutInflater.from(context))
        binding.familyTv.text = content.name
        if (content.type == 0){
            binding.letterPop1.text = content.content
            binding.letterPop1.visibility = View.VISIBLE
            binding.recordPop1.visibility = View.GONE
            binding.recordPop2.visibility = View.GONE
            binding.recordPop3.visibility = View.GONE
            binding.recordPop4.visibility = View.GONE
        }
        else{
            binding.letterPop1.visibility = View.GONE
            binding.recordPop1.visibility = View.VISIBLE
            binding.recordPop2.visibility = View.VISIBLE
            binding.recordPop3.visibility = View.VISIBLE
            binding.recordPop4.visibility = View.VISIBLE
        }

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setCanceledOnTouchOutside(true)

        dlg.show()
    }
}