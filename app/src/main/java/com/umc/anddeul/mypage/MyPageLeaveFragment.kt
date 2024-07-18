package com.umc.anddeul.mypage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.anddeul.R
import com.umc.anddeul.common.toast.AnddeulErrorToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentMypageLeaveBinding
import com.umc.anddeul.home.SaveDataHandler
import com.umc.anddeul.mypage.model.LeaveDTO
import com.umc.anddeul.mypage.model.SelectLeaderDTO
import com.umc.anddeul.mypage.network.LeaveInterface
import com.umc.anddeul.mypage.network.SelectLeaderInterface
import com.umc.anddeul.start.StartActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyPageLeaveFragment : Fragment() {
    lateinit var binding: FragmentMypageLeaveBinding
    var token: String? = null
    lateinit var retrofitBearer: Retrofit
    private lateinit var selectLeaderRVAdapter: SelectLeaderRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageLeaveBinding.inflate(inflater, container, false)

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        val memberDataHandler = SaveDataHandler(requireActivity())
        val leader = memberDataHandler.getFamilyLeader()
        val myNickname = memberDataHandler.getMyNickname()

        setToolbar()
        settingBtnColor()
        settingRVAdapter(leader!!, myNickname!!)

        return binding.root
    }

    private fun setToolbar() {
        binding.mypageLeaveBackIv.setOnClickListener {
            // MyPageSettingFragment로 이동
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    private fun settingBtnColor() {
        // 탈퇴하기 버튼 색상 설정
        binding.mypageLeaveReasonEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isButtonEnabled = s?.length ?: 0 >= 10
                binding.mypageLeaveBtn.isEnabled = isButtonEnabled
                binding.mypageLeaveBtn.backgroundTintList =
                    if (isButtonEnabled) ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary
                        )
                    )
                    else ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.icon_disabled
                        )
                    )
            }
        })
    }

    private fun settingBtn() {
        binding.mypageLeaveBtn.setOnClickListener {
            // 탈퇴 사유 내용이 10자 이상일 때만 버튼 클릭 가능
            if (binding.mypageLeaveReasonEdit.text.length >= 10) {
                leaveAnddeul()
            } else {
                Toast.makeText(requireContext(), "10자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun settingLeaderBtn(id: String) {
        binding.mypageLeaveBtn.setOnClickListener {
            if (binding.mypageLeaveReasonEdit.text.length >= 10) {
                selectLeader(id)
            } else {
                Toast.makeText(requireContext(), "10자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun settingRVAdapter(leader: String, myNickname: String) {
        if (leader != myNickname) {
            binding.mypageLeaveSelectLeaderTv.visibility = View.GONE
            binding.mypageLeaveSelectLeaderWarningTv.visibility = View.GONE
            binding.mypageLeaveSelectLeaderRv.visibility = View.GONE

            settingBtn()
        } else {
            val memberDataHandler = SaveDataHandler(requireActivity())
            val memberList = memberDataHandler.getMemberNames()

            selectLeaderRVAdapter = SelectLeaderRVAdapter(
                memberList,
                object : SelectLeaderRVAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val selectedMember = selectLeaderRVAdapter.getSelectedMember()
                        val memberId =
                            selectedMember?.let { memberDataHandler.getSnsIdByNickname(it) }
                        if (memberId != null) {
                            settingLeaderBtn(memberId)
                        }
                        Log.e("selectLeader", "이름: $selectedMember, memberId: $memberId")
                    }
                })
            binding.mypageLeaveSelectLeaderRv.layoutManager = LinearLayoutManager(context)
            binding.mypageLeaveSelectLeaderRv.adapter = selectLeaderRVAdapter
        }
    }

    private fun leaveAnddeul() {
        val leaveService = retrofitBearer.create(LeaveInterface::class.java)
        val leaveContent = binding.mypageLeaveReasonEdit.text.toString()

        leaveService.leaveApp(leaveContent).enqueue(object : Callback<LeaveDTO> {
            override fun onResponse(call: Call<LeaveDTO>, response: Response<LeaveDTO>) {
                Log.e("leaveService", "${response.code()}")
                Log.e("leaveService", "${response.body()}")

                if (response.isSuccessful) {
                    val intent = Intent(activity, StartActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                    startActivity(intent)
                } else {
                    Log.e("leaveService", "탈퇴 실패")
                    AnddeulErrorToast.createToast(requireActivity(), "다시 시도해 주세요")
                }
            }

            override fun onFailure(call: Call<LeaveDTO>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("leaveService", "Failure message: ${t.message}")
            }
        })
    }

    private fun selectLeader(userId: String) {
        val selectLeaderService = retrofitBearer.create(SelectLeaderInterface::class.java)

        selectLeaderService.selectLeader(userId).enqueue(object : Callback<SelectLeaderDTO> {
            override fun onResponse(
                call: Call<SelectLeaderDTO>,
                response: Response<SelectLeaderDTO>
            ) {
                Log.e("selectLeaderService", "${response.code()}")
                Log.e("selectLeaderService", "${response.body()}")
                if (response.isSuccessful) {
                    leaveAnddeul()
                } else {
                    AnddeulErrorToast.createToast(requireActivity(), "다시 시도해 주세요")
                }
            }

            override fun onFailure(call: Call<SelectLeaderDTO>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("selectLeaderService", "Failure message: ${t.message}")
            }
        })
    }
}