package com.umc.anddeul.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentDialogPermissionBinding
import com.umc.anddeul.ext.dialogResize
import retrofit2.Retrofit

interface DeleteDialogListener{
    fun onDelete(postId: Int)
}

class DeleteDialog(private val postId : Int, private val listener: DeleteDialogListener) : DialogFragment() {
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
            listener.onDelete(postId)
            dismiss()
        }

        // 취소 버튼
        binding.dialogConfirmCancelBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}