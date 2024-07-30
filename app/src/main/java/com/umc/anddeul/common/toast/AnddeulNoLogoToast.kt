package com.umc.anddeul.common.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.umc.anddeul.databinding.ToastNoLogoBinding
import com.umc.anddeul.ext.toPx

object AnddeulNoLogoToast {
    fun createNoLogoToast(context: Context, message: String): Toast {
        val inflater = LayoutInflater.from(context)
        val binding : ToastNoLogoBinding = ToastNoLogoBinding.inflate(inflater)

        binding.toastNoLogoTv.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 104.toPx())
            duration = Toast.LENGTH_LONG
            view = binding.root
        }
    }
}