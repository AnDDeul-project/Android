package com.umc.anddeul.mypage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageLeaveBinding
import com.umc.anddeul.home.model.LeaveDTO
import com.umc.anddeul.home.network.LeaveInterface
import com.umc.anddeul.start.StartActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageLeaveFragment : Fragment() {
    lateinit var binding: FragmentMypageLeaveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageLeaveBinding.inflate(inflater, container, false)

        // 탈퇴하기 버튼 색상 설정
        binding.mypageLeaveReasonEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isButtonEnabled = s?.length ?: 0 >= 10
                binding.mypageLeaveBtn.isEnabled = isButtonEnabled
                binding.mypageLeaveBtn.backgroundTintList = if (isButtonEnabled) ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
                else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icon_disabled))
            }
        })

        binding.mypageLeaveBtn.setOnClickListener {
            // 탈퇴 사유 내용이 10자 이상일 때만 버튼 클릭 가능
            if (binding.mypageLeaveReasonEdit.text.length >= 10) {
                // 탈퇴 api 연결
                leaveAnddeul()
            } else {
                // 10자 미만인 경우 사용자에게 메시지 표시 또는 다른 조치 수행
                Toast.makeText(requireContext(), "10자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageLeaveToolbar.apply {
            setNavigationIcon(R.drawable.mypage_setting_back)
            setNavigationOnClickListener {
                // MyPageSettingFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }

    fun leaveAnddeul() {
        val spf: SharedPreferences = requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNzc1MjQ1OH0.gv84EPPvswVZnhSp6KAaNSGCx6oDoYXR37e46cGxvvo"

        val retrofitBearer = Retrofit.Builder()
            .baseUrl("http://umc-garden.store")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.orEmpty())
                            .build()
                        Log.d("retrofitBearer", "Token: ${token.toString()}" + token.orEmpty())
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        val leaveService = retrofitBearer.create(LeaveInterface::class.java)

        leaveService.leaveApp().enqueue(object : Callback<LeaveDTO> {
            override fun onResponse(call: Call<LeaveDTO>, response: Response<LeaveDTO>) {
                Log.e("leaveService", "onResponse")
                Log.e("leaveService", "${response.code()}")
                Log.e("leaveService", "${response.body()}")

                if (response.isSuccessful) {
                    // 로그인 화면으로 이동
                    val intent = Intent(activity, StartActivity::class.java)
                    // FLAG_ACTIVITY_NO_HISTORY 플래그를 사용하여 해당 액티비티를 백 스택에서 제거
                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                    startActivity(intent)
                } else {
                    Log.e("leaveService", "탈퇴 실패")
                }
            }

            override fun onFailure(call: Call<LeaveDTO>, t: Throwable) {
                Log.e("leaveService", "onFailure")
                Log.e("leaveService", "Failure message: ${t.message}")
            }

        })
    }
}