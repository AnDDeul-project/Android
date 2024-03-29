package com.umc.anddeul.mypage

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageModifyProfileBinding
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PermissionDialog
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
import java.io.ByteArrayOutputStream
import java.io.File

class MyPageModifyFragment : Fragment() {
    lateinit var binding: FragmentMypageModifyProfileBinding
    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되면 갤러리 액티비티로 이동
                openGallery()
            } else {
                val permissionDialog = PermissionDialog()
                permissionDialog.isCancelable = false
                permissionDialog.show(parentFragmentManager, "permission dialog")
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 갤러리에서 선택한 이미지의 Uri를 가져옵니다.
                val selectedImageUri: Uri? = result.data?.data

                // 선택한 이미지 화면에 동그랗게 크롭 후 띄우기
                val imageView = binding.mypageModifyProfileIv
                selectedImageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imageView)
                }

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
        val myProfileData: UserProfileData? = myPageViewModel.getMyProfile()

        // 프로필 이미지, 닉네임 정보 담아 띄우기
        val imageView = binding.mypageModifyProfileIv
        val loadImage = LoadProfileImage(imageView)
        loadImage.execute(myProfileData?.image)

        binding.mypageModifyUsername.setText(myProfileData?.nickname)

        // 프로필 사진 선택 시 갤러리 권한 확인
        binding.mypageModifyProfileIv.setOnClickListener {
            checkPermission()
        }

        // 사진 수정하기 글씨 클릭 시 갤러리 권한 확인
        binding.mypageModifyProfileTv.setOnClickListener {
            checkPermission()
        }

        val imagePath = myProfileData!!.image
        val profileImageUri = Uri.parse(imagePath)

        // 이름만 변경할 때
        binding.mypageModifyStoreBtn.setOnClickListener {
            modifyMyProfile(profileImageUri)
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

    // 이미지 품질 암축
    fun compressImage(context: Context, uri: Uri, quality: Int): ByteArray {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // 샘플링 크기를 조정하여 이미지 크기를 줄임

        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        val byteArrayOutputStream = ByteArrayOutputStream()

        // 이미지를 JPEG 형식으로 압축
        bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }

    fun modifyMyProfile(newImage: Uri) {
        val spf: SharedPreferences =
            requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
        val token = spf.getString("jwtToken", "")

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
        val compressedImage = compressImage(requireContext(), newImage, 30) // 이미지 품질 30으로 설정
        val requestImage = RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedImage)
        val newProfileImage = MultipartBody.Part.createFormData("image", file.name, requestImage)


        val newUsername = binding.mypageModifyUsername.text.toString()
        val usernameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), newUsername)

        modifyService.modifyProfile(usernameRequestBody, newProfileImage)
            .enqueue(object : Callback<ModifyProfileResponse> {
                override fun onResponse(
                    call: Call<ModifyProfileResponse>,
                    response: Response<ModifyProfileResponse>
                ) {
                    Log.e("modifyProfileService", "${response.code()}")
                    Log.e("modifyProfileService", "${response.body()}")

                    if (response.isSuccessful) {
                        // MyPageFragment로 이동
                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.mypage_modify_profile_layout, MyPageFragment())
                            .commit()

                    } else {
                        Log.e("modifyProfileService", "프로필 수정 실패")
                    }
                }

                override fun onFailure(call: Call<ModifyProfileResponse>, t: Throwable) {
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
                openGallery()
            } else {
                // 권한이 없는 경우 권한 요청
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }
        }

        // 안드로이드 SDK가 33 이상인 경우
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permissionImagesGranted && permissionVideosGranted) {
                // 이미 권한이 허용된 경우 해당 코드 실행
                openGallery()
            } else {
                // 권한이 없는 경우 권한 요청
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else { // 안드로이드 SDK가 33보다 낮은 경우
            if (permissionReadExternalGranted) {
                // 이미 권한이 허용된 경우 해당 코드 실행
                openGallery()
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