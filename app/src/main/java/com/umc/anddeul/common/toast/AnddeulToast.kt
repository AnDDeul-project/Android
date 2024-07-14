package com.umc.anddeul.common

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.umc.anddeul.databinding.ToastAnddeulBinding
import com.umc.anddeul.ext.toPx

object AnddeulToast {
    fun createToast(context: Context, message: String): Toast {
        val inflater = LayoutInflater.from(context)
        val binding : ToastAnddeulBinding = ToastAnddeulBinding.inflate(inflater)

        binding.toastAnddeulTv.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 104.toPx())
            duration = Toast.LENGTH_LONG
            view = binding.root
        }
    }
}