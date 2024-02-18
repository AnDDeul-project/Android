package com.umc.anddeul.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.AddChecklistActivity
import com.umc.anddeul.databinding.FragmentUserProfileBinding
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.network.UserProfileInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileFragment : Fragment() {
    lateinit var binding: FragmentUserProfileBinding
    private var gson : Gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        // 선택한 유저의 아이디 가져오기
        val idJson = arguments?.getString("selectedId")
        val snsId = gson.fromJson(idJson, String::class.java)
        Log.e("userProfileService", "선택된 Id : ${snsId}")

        binding.userProfileToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                // homeFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }

        loadProfile(snsId)

        return  binding.root
    }

    fun loadProfile(snsId : String) {
        val spf: SharedPreferences =
            requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
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

        val userProfileService = retrofitBearer.create(UserProfileInterface::class.java)

        userProfileService.getUserProfile(snsId).enqueue(object : Callback<UserProfileDTO> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<UserProfileDTO>,
                response: Response<UserProfileDTO>
            ) {
                Log.e("userProfileService", "onResponse")
                Log.e("userProfileService response code : ", "${response.code()}")
                Log.e("userProfileService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val userProfileData = response.body()?.result

                    userProfileData?.let {
                        binding.userProfileUsernameTv.text = userProfileData.nickname
                        binding.userProfilePostNumTv.text = "게시물 ${userProfileData.postCount}개"

                        val userProfileRVAdapter = UserProfileRVAdapter(userProfileData.firstPostImages, userProfileData.postIdx)
                        Log.e("UserProfileBind", "게시물 첫번째 사진들 리스트 : ${userProfileData.firstPostImages}")

                        binding.userProfilePostRv.layoutManager = GridLayoutManager(requireContext(),3)
                        binding.userProfilePostRv.adapter = userProfileRVAdapter

                        userProfileRVAdapter.setMyItemClickListener(object : UserProfileRVAdapter.MyItemClickInterface {
                            override fun onItemClick(postIdx: Int) {
                                // 선택한 게시글 단일 조회
                                getPost(postIdx)
                            }
                        })

                        val profileImageUrl = userProfileData.image
                        val imageView = binding.userProfileIv
                        val loadImage = LoadProfileImage(imageView)
                        loadImage.execute(profileImageUrl)

                        binding.userProfileCheckIv.setOnClickListener {
                            // 체크리스트 화면으로 이동
                            val intent = Intent(context, AddChecklistActivity::class.java)
                            intent.putExtra("checkUserId", snsId)
                            intent.putExtra("checkUserName", userProfileData.nickname)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                Log.e("userProfileService", "onFailure")
                Log.e("userProfileService", "Failure message: ${t.message}")

            }
        })
    }

    fun getPost(postIdx: Int) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.user_profile_layout, UserPostFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val postIdxJson = gson.toJson(postIdx)
                    putInt("postIdx", postIdxJson.toInt())
                }
            })
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}