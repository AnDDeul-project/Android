package com.umc.anddeul.postbox

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.umc.anddeul.databinding.FragmentPopupRecordBinding

class RecordPopupFragment(private val context: Context) {
    private lateinit var binding: FragmentPopupRecordBinding
    private val dlg = Dialog(context)

    fun show(){
        binding = FragmentPopupRecordBinding.inflate(LayoutInflater.from(context))

        // 기본 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(true)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window?.setGravity(Gravity.BOTTOM)

        dlg.setCanceledOnTouchOutside(true)

        // 녹음 시작 버튼
        binding.recordPlayBtn.setOnClickListener {
            binding.recordPlayBtn.visibility = View.GONE
            binding.recordPauseBtn.visibility = View.VISIBLE
        }

        // 녹음 정지 버튼
        binding.recordPauseBtn.setOnClickListener {
            binding.recordPlayBtn.visibility = View.VISIBLE
            binding.recordPauseBtn.visibility = View.GONE
        }

        // 확인 버튼
        binding.okBtn.setOnClickListener {
            // 녹음 전달 코드 추가
            dlg.dismiss()
        }

        // 재녹음 버튼
        binding.restartBtn.setOnClickListener {
            val restartFragment = DialogRecordRestartFragment(context)
            restartFragment.show()
        }

        dlg.show()
    }
}