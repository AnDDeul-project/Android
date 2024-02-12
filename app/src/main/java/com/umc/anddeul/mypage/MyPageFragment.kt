package com.umc.anddeul.mypage

import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageBinding
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.PostUploadActivity
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.UserPostFragment
import com.umc.anddeul.home.UserProfileRVAdapter
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.UserProfileInterface
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
    private val myPageViewModel : MyPageViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되면 갤러리 액티비티로 이동
                val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                startActivity(postUploadActivity)
            } else {
                val permissionDialog = PermissionDialog()
                permissionDialog.isCancelable = false
                permissionDialog.show(parentFragmentManager, "permission dialog")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.mypageSettingIb.setOnClickListener {
            // MyPageSettingFragment로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_layout, MypageSettingFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 게시글 올리기
        binding.mypageUploadBtn.setOnClickListener {
            checkPermission()
        }

        // 프로필 수정하기
        binding.mypageModifyBtn.setOnClickListener {
            // MyPageModifyFragment로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_layout, MyPageModifyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        loadMyProfile()

        return binding.root
    }

    fun checkPermission() {
        val permissionImages = android.Manifest.permission.READ_MEDIA_IMAGES
        val permissionVideos = android.Manifest.permission.READ_MEDIA_VIDEO
        val permissionUserSelected = android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionImagesGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionImages
        ) == PackageManager.PERMISSION_GRANTED

        val permissionVideosGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionVideos
        ) == PackageManager.PERMISSION_GRANTED

        val permissionUserSelectedGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionUserSelected
        ) == PackageManager.PERMISSION_GRANTED

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        // SDK 34 이상
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (permissionImagesGranted && permissionVideosGranted && permissionUserSelectedGranted) {
                // 이미 권한이 허용된 경우 해당 코드 실행
                val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                startActivity(postUploadActivity)
            } else {
                // 권한이 없는 경우 권한 요청
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
        }

        // 안드로이드 SDK가 33 이상인 경우
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permissionImagesGranted && permissionVideosGranted) {
                // 이미 권한이 허용된 경우 해당 코드 실행
                val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                startActivity(postUploadActivity)
            } else {
                // 권한이 없는 경우 권한 요청
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else { // 안드로이드 SDK가 33보다 낮은 경우
            if (permissionReadExternalGranted) {
                // 이미 권한이 허용된 경우 해당 코드 실행
                val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                startActivity(postUploadActivity)
            } else {
                // 권한이 없는 경우 권한 요청
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // 내 프로필 조회
    fun loadMyProfile() {
        // 내 sns id 가져오기
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "not found")

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

        val userProfileService = retrofitBearer.create(UserProfileInterface::class.java)

        if (myId != null) {
            userProfileService.getUserProfile(myId).enqueue(object : Callback<UserProfileDTO> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<UserProfileDTO>,
                    response: Response<UserProfileDTO>
                ) {
                    Log.e("myProfileService", "onResponse")
                    Log.e("myProfileService response code : ", "${response.code()}")
                    Log.e("myProfileService response body : ", "${response.body()}")

                    if (response.isSuccessful) {
                        val myProfileData = response.body()?.result

                        myProfileData?.let {
                            binding.mypageUsernameTv.text = myProfileData.nickname
                            binding.mypagePostNumTv.text = "게시물 ${myProfileData.postCount}개"

                            val myProfileRVAdapter = UserProfileRVAdapter(myProfileData.firstPostImages, myProfileData.postIdx)

                            binding.mypageProfileRv.layoutManager = GridLayoutManager(requireContext(), 3)
                            binding.mypageProfileRv.adapter = myProfileRVAdapter

                            myProfileRVAdapter.setMyItemClickListener(object : UserProfileRVAdapter.MyItemClickInterface {
                                override fun onItemClick(postIdx: Int) {
                                    // 선택한 게시글 단일 조회
                                    getPost(postIdx)
                                }
                            })

                            val profileImageUrl = myProfileData.image
                            val imageView = binding.mypageProfileIv
                            val loadImage = LoadProfileImage(imageView)
                            loadImage.execute(profileImageUrl)

                            val gson = Gson()
                            val myProfile = myProfileData
                            val myProfileDTO = gson.fromJson(gson.toJson(myProfile), UserProfileData::class.java)
                            myPageViewModel.setMyProfile(myProfileDTO)

                        }
                    }
                }

                override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
                    Log.e("myProfileService", "onFailure")
                    Log.e("myProfileService", "Failure message: ${t.message}")
                }
            })
        }
    }

    fun getPost(postIdx: Int) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.mypage_layout, MyPostFragment().apply {
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