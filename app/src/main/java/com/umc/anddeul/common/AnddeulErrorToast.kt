package com.umc.anddeul.common

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.umc.anddeul.databinding.ToastErrorAnddeulBinding

object AnddeulErrorToast {
    fun createToast(context: Context, message: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding : ToastErrorAnddeulBinding = ToastErrorAnddeulBinding.inflate(inflater)

        binding.toastErrorTv.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 104.toPx())
            duration = Toast.LENGTH_LONG
            view = binding.root
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}