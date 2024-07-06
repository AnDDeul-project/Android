package com.umc.anddeul.mypage

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.common.AnddeulErrorToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentMypageModifyProfileBinding
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.model.ModifyProfileResponse
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.ModifyProfileInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File

class MyPageModifyFragment : Fragment() {
    lateinit var binding: FragmentMypageModifyProfileBinding
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    var token: String? = null
    lateinit var retrofitBearer: Retrofit

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
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
                val selectedImageUri: Uri? = result.data?.data

                val imageView = binding.mypageModifyProfileIv
                selectedImageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imageView)
                }

                binding.mypageModifyStoreBtn.setOnClickListener {
                    if (selectedImageUri != null) {
                        modifyMyProfile(selectedImageUri, binding.mypageModifyUsername.text.toString())
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

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

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

        // 이름만 변경할 때
        binding.mypageModifyStoreBtn.setOnClickListener {
            modifyMyProfile(null, binding.mypageModifyUsername.text.toString())
        }

        setToolbar()

        return binding.root
    }

    fun setToolbar() {
        binding.mypageModifyProfileBackIv.setOnClickListener {
            // MyPageFragment로 이동
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
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

    // 이미지 품질 압축
    fun compressImage(file: File, quality: Int): ByteArray {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // 샘플링 크기를 조정하여 이미지 크기를 줄임

        val bitmap = BitmapFactory.decodeFile(file.path, options)
        val byteArrayOutputStream = ByteArrayOutputStream()

        // 이미지를 JPEG 형식으로 압축
        bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }

    fun modifyMyProfile(profileUri: Uri?, nickName: String) {
        val modifyService = retrofitBearer.create(ModifyProfileInterface::class.java)

        val newProfileImage = profileUri?.let { uri ->
            val file = getFileFromUri(requireContext(), uri)
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressImage(file, 30))
            )
        }
        val usernameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nickName)

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
                            .replace(R.id.main_frm, MyPageFragment())
                            .commit()
                    } else {
                        context?.let { AnddeulErrorToast.createToast(it, "다시 시도해 주세요").show() }
                        Log.e("modifyProfileService", "프로필 수정 실패")
                    }
                }

                override fun onFailure(call: Call<ModifyProfileResponse>, t: Throwable) {
                    context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
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