package com.umc.anddeul.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.mypage.model.LogoutDTO
import com.umc.anddeul.mypage.network.LogoutInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LogoutDialog : DialogFragment() {
    lateinit var binding: FragmentDialogPermissionBinding
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    override fun onResume() {
        super.onResume()

        // 다이얼로그 크기 조정
        context?.dialogResize(requireDialog(),0.9f)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPermissionBinding.inflate(layoutInflater, container, false)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogPermissionTv.text = "로그아웃 하시겠어요?"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            // 로그아웃 api 연결
            myPageLogout()
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun myPageLogout() {
        val logoutService = retrofitBearer.create(LogoutInterface::class.java)

        logoutService.logout().enqueue(object : Callback<LogoutDTO> {
            override fun onResponse(call: Call<LogoutDTO>, response: Response<LogoutDTO>) {
                Log.e("logoutService", "${response.code()}")
                Log.e("logoutService", "${response.body()}")

                if (response.isSuccessful) {
                    dismiss()

                    // 토큰 초기화
                    val spf = requireActivity().getSharedPreferences("myToken", AppCompatActivity.MODE_PRIVATE)
                    val editor = spf.edit()
                    editor.putString("jwtToken", "")
                    editor.apply()

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, StartActivity::class.java)
                    startActivity(intent)

                } else {
                    Log.e("logoutService", "로그아웃 실패")
                }
            }

            override fun onFailure(call: Call<LogoutDTO>, t: Throwable) {
                Log.e("logoutService", "Failure message: ${t.message}")
            }
        })
    }

    fun Context.dialogResize(dialog: Dialog, width: Float){
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val originalLayoutParams = dialog.window?.attributes

        val originalHeight = originalLayoutParams?.height ?: WindowManager.LayoutParams.WRAP_CONTENT

        if (Build.VERSION.SDK_INT < 30){
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog.window
            val x = (size.x * width).toInt()

            window?.setLayout(x, originalHeight)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            val x = (rect.width() * width).toInt()

            window?.setLayout(x, originalHeight)
        }
    }
}