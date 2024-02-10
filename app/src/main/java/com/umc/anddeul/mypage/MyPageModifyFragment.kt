package com.umc.anddeul.mypage

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageModifyProfileBinding
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.PostUploadActivity
import com.umc.anddeul.home.model.ModifyProfileResponse
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.ModifyProfileInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MyPageModifyFragment : Fragment() {
    lateinit var binding: FragmentMypageModifyProfileBinding
    private val PICK_IMAGE_REQUEST = 123


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한이 허용되면 갤러리 액티비티로 이동
            openGallery()
        } else {
            val permissionDialog = PermissionDialog()
            permissionDialog.isCancelable = false
            permissionDialog.show(parentFragmentManager, "permission dialog")
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 갤러리에서 선택한 이미지의 Uri를 가져옵니다.
            val selectedImageUri: Uri? = result.data?.data

            binding.mypageModifyStoreBtn.setOnClickListener {
                if (selectedImageUri != null) {
                    modifyMyProfile(selectedImageUri)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageModifyProfileBinding.inflate(inflater, container, false)
        val myPageViewModel : MyPageViewModel by viewModels({requireParentFragment()})
        val myProfileData : UserProfileData? = myPageViewModel.getMyProfile().value

        // 프로필 이미지, 닉네임 정보 담아 띄우기
        val imageView = binding.mypageModifyProfileIv
        val loadImage = LoadProfileImage(imageView)
        loadImage.execute(myProfileData?.image)

        binding.mypageModifyUsername.setText(myProfileData?.nickname)

        binding.mypageModifyProfileIv.setOnClickListener {
            checkPermission()
        }

        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageModifyProfileToolbar.apply {
            setNavigationIcon(R.drawable.mypage_setting_back)
            setNavigationOnClickListener {
                // MyPageFragment로 이동
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val filePath = it.getString(columnIndex)
            return File(filePath)
        }

        throw IllegalArgumentException("Invalid URI")
    }

    fun modifyMyProfile(newImage : Uri) {
        val spf: SharedPreferences = requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        // val token = spf.getString("jwtToken", "")
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrYWthb19pZCI6WyIzMzA0MTMzMDkzIl0sImlhdCI6MTcwNzExNDkyMn0.xUiMr__vOcdjOVjcrmV3HiuWOqatI1PPmSPgJFljwTw"

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

        val modifyService = retrofitBearer.create(ModifyProfileInterface::class.java)

        val file = getFileFromUri(requireContext(), newImage)
        val requestImage = file.asRequestBody("image/".toMediaTypeOrNull())
        val newProfileImage = MultipartBody.Part.createFormData("image", file.name, requestImage)

        val newUsername = binding.mypageModifyUsername.text.toString()
        val usernameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), newUsername)

        Log.e("modifyProfileService", "${usernameRequestBody}, ${newUsername}")

        modifyService.modifyProfile(usernameRequestBody, newProfileImage).enqueue(object : Callback<ModifyProfileResponse> {
            override fun onResponse(
                call: Call<ModifyProfileResponse>,
                response: Response<ModifyProfileResponse>
            ) {
                Log.e("modifyProfileService", "onResponse")
                Log.e("modifyProfileService", "${response.code()}")
                Log.e("modifyProfileService", "${response.body()}")

                if (response.isSuccessful) {
                    Log.e("modifyProfileService", "프로필 수정 성공")

                    // MyPageFragment로 이동
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.mypage_modify_profile_layout, MyPageFragment())
                        .commit()
                } else {
                    Log.e("modifyProfileService", "프로필 수정 실패")
                }
            }

            override fun onFailure(call: Call<ModifyProfileResponse>, t: Throwable) {
                Log.e("modifyProfileService", "onFailure")
                Log.e("modifyProfileService", "Failure message: ${t.message}")

            }

        })
    }

    // 갤러리 접근 권한 확인 함수
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

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }
}