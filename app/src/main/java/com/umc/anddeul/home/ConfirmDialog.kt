package com.umc.anddeul.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.DialogConfirmBinding
import com.umc.anddeul.home.model.MemberApproveDTO
import com.umc.anddeul.home.network.MemberApproveInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

interface ConfirmDialogListener {
    fun onAcceptClicked()
    fun onCancelClicked()
}

class ConfirmDialog(name: String, groupName: String, userId: String, private val listener: ConfirmDialogListener) : DialogFragment() {
    lateinit var binding : DialogConfirmBinding
    private var name : String = ""
    private var groupName : String = ""
    private var userId : String = ""
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    init {
        this.name = name
        this.groupName = groupName
        this.userId = userId
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmBinding.inflate(layoutInflater, container, false)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        binding.dialogNameTv.text = "'${name}'님을"
        binding.dialogQuestionTv.text = "${groupName}에 추가하시겠습니까?"

        // 취소 버튼
        binding.dialogCancelBtn.setOnClickListener {
            dismiss()
        }

        // 수락하기 버튼
        binding.dialogAcceptBtn.setOnClickListener {
            approveMember(userId)
        }

        return binding.root
    }

    fun approveMember(userId: String) {
        val approveService = retrofitBearer.create(MemberApproveInterface::class.java)

        approveService.approveMember(userId).enqueue(object : Callback<MemberApproveDTO> {
            override fun onResponse(
                call: Call<MemberApproveDTO>,
                response: Response<MemberApproveDTO>
            ) {
                Log.e("approveService response code : ", "${response.code()}")
                Log.e("approveService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    dismiss()
                    listener.onAcceptClicked()
                }
            }

            override fun onFailure(call: Call<MemberApproveDTO>, t: Throwable) {
                Log.e("approveService", "Failure message: ${t.message}")
            }
        })
    }
}