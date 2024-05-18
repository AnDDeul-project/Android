package com.umc.anddeul.mypage

import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentMypageBinding
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.LoadProfileImage
import com.umc.anddeul.home.PostWriteActivity
import com.umc.anddeul.home.UserProfileRVAdapter
import com.umc.anddeul.home.model.UserProfileDTO
import com.umc.anddeul.home.model.UserProfileData
import com.umc.anddeul.home.network.UserProfileInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    var token: String? = null
    lateinit var retrofitBearer: Retrofit
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    private val selectedImages: ArrayList<Uri> = ArrayList()
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>

    // 이미지 등록 가능 갯수
    private val imageUploadPossible = 10

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startAlbumLauncher()
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

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        binding.mypageSettingIb.setOnClickListener {
            // MyPageSettingFragment로 이동
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.mypage_layout, MypageSettingFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 게시글 올리기
        binding.mypageUploadBtn.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                startPhotoPicker()
            } else {
                checkPermission()
            }
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

        // 포토피커 세팅
        settingPhotoPicker(imageUploadPossible)
        // 갤러리 런처 세팅
        settingAlbumLauncher(imageUploadPossible)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadMyProfile()
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

    // 갤러리 접근 권한 확인 함수
    fun checkPermission() {
        val permissionReadExternal = android.Manifest.permission.READ_EXTERNAL_STORAGE

        val permissionReadExternalGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionReadExternal
        ) == PackageManager.PERMISSION_GRANTED

        // 포토피커를 사용하지 못하는 버전만 권한 확인 (SDK 30 미만)
        if (permissionReadExternalGranted) {
            startAlbumLauncher()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // 내 프로필 조회
    fun loadMyProfile() {
        // 내 sns id 가져오기
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "not found")

        val userProfileService = retrofitBearer.create(UserProfileInterface::class.java)

        if (myId != null) {
            userProfileService.getUserProfile(myId).enqueue(object : Callback<UserProfileDTO> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<UserProfileDTO>,
                    response: Response<UserProfileDTO>
                ) {
                    Log.e("myProfileService response code : ", "${response.code()}")
                    Log.e("myProfileService response body : ", "${response.body()}")

                    if (response.isSuccessful) {
                        val myProfileData = response.body()?.result

                        myProfileData?.let {
                            binding.mypageUsernameTv.text = myProfileData.nickname
                            binding.mypagePostNumTv.text = "게시물 ${myProfileData.postCount}개"

                            val myProfileRVAdapter = UserProfileRVAdapter(
                                myProfileData.firstPostImages,
                                myProfileData.postIdx
                            )

                            binding.mypageProfileRv.layoutManager =
                                GridLayoutManager(requireContext(), 3)
                            binding.mypageProfileRv.adapter = myProfileRVAdapter

                            myProfileRVAdapter.setMyItemClickListener(object :
                                UserProfileRVAdapter.MyItemClickInterface {
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
                            val myProfileDTO =
                                gson.fromJson(gson.toJson(myProfile), UserProfileData::class.java)
                            myPageViewModel.setMyProfile(myProfileDTO)

                        }
                    }
                }

                override fun onFailure(call: Call<UserProfileDTO>, t: Throwable) {
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

    @SuppressLint("IntentReset")
    fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // 이미지 여러개 선택 가능
        albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        // 액티비티를 실행한다.
        albumLauncher.launch(albumIntent)
    }

    fun startPhotoPicker() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // 포토피커 설정
    fun settingPhotoPicker(imageUploadPossible: Int) {
        pickMultipleMedia = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(
                imageUploadPossible
            )
        ) { uris ->
            // 선택한 이미지들의 URI 목록을 처리하는 콜백
            if (uris.isNotEmpty()) {
                // 선택한 이미지가 있을 경우
                val selectedImagesList = ArrayList(uris)

                // 다음 액티비티로 전달할 intent 생성
                val intent = Intent(requireContext(), PostWriteActivity::class.java)
                intent.putParcelableArrayListExtra("selectedImages", selectedImagesList)

                // 선택한 이미지들을 다음 액티비티로 전달
                startActivity(intent)
            } else {
                // 선택한 이미지가 없을 경우
            }
        }
    }

    // 앨범 런처 설정
    fun settingAlbumLauncher(imageUploadPossible: Int) {
        // 앨범 실행을 위한 런처
        val contract2 = ActivityResultContracts.StartActivityForResult()
        albumLauncher = registerForActivityResult(contract2) {
            // 사진 선택을 완료한 후 돌아왔다면
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지의 경로 데이터를 관리하는 Uri 객체 리스트를 추출
                val uriclip = it.data?.clipData
                uriclip?.let { clipData ->
                    for (i in 0 until uriclip!!.itemCount) {
                        if (selectedImages.size >= imageUploadPossible) {
                            Snackbar.make(
                                binding.root,
                                "사진 첨부는 최대 ${imageUploadPossible}장까지 가능합니다.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            break
                        } else {
                            val uri = uriclip.getItemAt(i).uri
                            // 한 장씩 리스트에 추가
                            selectedImages.add(uri)
                        }
                    }
                }
                if (selectedImages.isNotEmpty()) {
                    // 글 작성 페이지로 이동
                    val intent = Intent(requireContext(), PostWriteActivity::class.java)

                    // 선택된 이미지들을 번들에 추가
                    val selectedImagesUri: MutableList<Uri> = ArrayList()
                    for (selectedImageUri in selectedImages) {
                        selectedImagesUri.add(selectedImageUri)
                    }
                    intent.putParcelableArrayListExtra("selectedImages", ArrayList(selectedImagesUri))

                    // 다음 액티비티 시작
                    startActivity(intent)
                }
            }
        }
    }
}