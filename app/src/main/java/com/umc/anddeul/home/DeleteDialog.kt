package com.umc.anddeul.home

import android.app.Dialog
import android.content.Context
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
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.common.AnddeulErrorToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.home.model.PostDelete
import com.umc.anddeul.home.network.PostDeleteInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DeleteDialog(val postId : Int) : DialogFragment() {
    lateinit var binding: FragmentDialogPermissionBinding
    var token : String? = null
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
        binding.dialogPermissionTv.text = "게시물을 삭제 하시겠어요?"

        // 확인 버튼
        binding.dialogConfirmBtn.setOnClickListener {
            delete(postId)
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun delete(postId : Int) {
        val deleteService = retrofitBearer.create(PostDeleteInterface::class.java)

        deleteService.deletePost(postId).enqueue(object : Callback<PostDelete> {
            override fun onResponse(call: Call<PostDelete>, response: Response<PostDelete>) {
                Log.e("deleteService response code : ", "${response.code()}")
                Log.e("deleteService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    // 게시글 삭제 응답 코드가 200이라면 다이얼로그를 닫고 HomeFragment의 loadPost 함수 호출
                    if (deleteResponse!!.code == 2000) {
                        dismiss()

                        // 홈 프래그먼트의 loadPost 함수 호출해서 게시글 새로고침
                        val homeFragment = parentFragment as? HomeFragment
                        homeFragment?.loadPost(0)
                    }
                }
            }

            override fun onFailure(call: Call<PostDelete>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("deleteService", "Failure message: ${t.message}")
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