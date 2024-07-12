package com.umc.anddeul.home

import PostsInterface
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.checklist.AddChecklistActivity
import com.umc.anddeul.common.AnddeulErrorToast
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.FragmentHomeBinding
import com.umc.anddeul.databinding.FragmentHomeMenuMemberBinding
import com.umc.anddeul.databinding.FragmentHomeMenuRequestMemberBinding
import com.umc.anddeul.ext.addOnScrollEndListener
import com.umc.anddeul.home.model.Member
import com.umc.anddeul.home.model.MemberResponse
import com.umc.anddeul.home.model.Post
import com.umc.anddeul.home.model.PostData
import com.umc.anddeul.home.network.MemberInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class HomeFragment : Fragment(), ConfirmDialogListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var postRVAdapter: PostRVAdapter
    lateinit var drawerLayout: DrawerLayout
    var token: String? = null
    lateinit var retrofitBearer: Retrofit
    private var page: Int = 0
    private var isLastPage: Boolean = false

    private val pickMultipleMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_UPLOAD_IMAGE)
    ) { uris ->
        // 선택한 이미지들의 URI 목록을 처리하는 콜백
        if (uris.isNotEmpty()) {
            // 선택한 이미지가 있을 경우
            val selectedImagesList = ArrayList(uris)

            startActivity(Intent(requireContext(), PostWriteActivity::class.java).apply {
                putParcelableArrayListExtra("selectedImages", selectedImagesList)
            })
        } else {
            // 선택한 이미지가 없을 경우
        }
    }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // 사진 선택을 완료한 후 돌아왔다면
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                // 선택한 이미지의 경로 데이터를 관리하는 Uri 객체 리스트를 추출
                val uriclip = it.data?.clipData
                val selectedImages: List<Uri> = if (uriclip == null) {
                    emptyList()
                } else {
                    List(uriclip.itemCount) { index -> uriclip.getItemAt(index).uri }
                }
                if (selectedImages.size > MAX_UPLOAD_IMAGE) {
                    Snackbar.make(
                        binding.root,
                        "사진 첨부는 최대 ${MAX_UPLOAD_IMAGE}장까지 가능합니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                if (selectedImages.isNotEmpty()) {
                    startActivity(Intent(requireContext(), PostWriteActivity::class.java).apply {
                        putParcelableArrayListExtra(
                            "selectedImages",
                            ArrayList(selectedImages.take(MAX_UPLOAD_IMAGE)) // take API 살펴보기
                        )
                    })
                }
            }
        }

    companion object {
        // 이미지 등록 가능 갯수
        const val MAX_UPLOAD_IMAGE = 10
    }

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        postRVAdapter = PostRVAdapter(requireContext(), listOf(), listOf()) // 어댑터와 postDatas 연결
        binding.homeFeedRv.adapter = postRVAdapter // recyclerView에 Adapter 연결
        binding.homeFeedRv.layoutManager = LinearLayoutManager(context)

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = binding.homeDrawerLayout

        token = TokenManager.getToken()
        retrofitBearer = RetrofitManager.getRetrofitInstance()

        // 게시글 조회
        loadPost(0)
        // 메뉴 가족 구성원 정보 가져오기
        loadMemberList()

        binding.homeFeedRv.addOnScrollEndListener {
            if (!isLastPage) {
                loadPost(page + 1)
            }
        }

        binding.homeToolbarMenuIb.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END)
            } else {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        // 유저 프로필 이미지 클릭 시 유저 프로필 조회로 이동
        binding.homeMenuMyProfileIv.setOnClickListener {
            // drawerLayout 자동 닫기
            drawerLayout.closeDrawers()

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.home_drawer_layout, UserProfileFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // swipe refresh layout 초기화 (swipe 해서 피드 새로고침)
        binding.homeSwipeRefresh.setOnRefreshListener {
            loadPost(0)
        }

        // Floating Action Button 클릭 시
        binding.homeFloatingBt.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                startPhotoPicker()
            } else {
                checkPermission()
            }
        }

        postRVAdapter.setMyItemClickListener(object : PostRVAdapter.MyItemClickListener {
            override fun onItemClick(userId: String) {
                // 선택한 유저 프로필로 이동
                changeUserProfile(userId)
            }

            override fun onDeleteClick(postId: Int) {
                val deleteDialog = DeleteDialog(postId)
                deleteDialog.isCancelable = false
                deleteDialog.show(requireActivity().supportFragmentManager, "delete dialog")

            }

            override fun onModifyClick(
                postId: Int,
                selectedImages: List<String>,
                postContent: String
            ) {
                val intent = Intent(requireContext(), PostModifyActivity::class.java)

                intent.putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                intent.putExtra("postId", postId)
                intent.putExtra("postContent", postContent)

                // 다음 액티비티 시작
                startActivity(intent)
            }
        })
        return binding.root
    }

    fun saveMyId(context: Context, myId: String) {
        val spfMyId = context.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val editor = spfMyId.edit()
        editor.putString("myId", myId)
        editor.apply()
    }

    fun loadPost(page: Int) {
        // 내 sns id 가져오기
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "not found")

        val postService = retrofitBearer.create(PostsInterface::class.java)

        postService.homePosts(page).enqueue(object : Callback<Post> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.e("postService response code : ", "${response.code()}")
                Log.e("postService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val count = response.body()?.result!!.count
                    if (count < 20 || count == 0) {
                        isLastPage = true
                    }

                    val postData = response.body()?.result!!.data.map {
                        PostData(
                            it.post_idx,
                            it.user_idx,
                            it.nickname,
                            it.content,
                            it.picture,
                            it.userImage,
                            it.emojis
                        )
                    }

                    // 각 게시글의 작성자 타입을 확인하여 리스트에 저장
                    val authorTypesList = postData.map { post ->
                        if (myId == post.user_idx) {
                            "me"
                        } else {
                            "other"
                        }
                    }
                    postRVAdapter.authorTypeList = authorTypesList
                    postRVAdapter.postList = postData
                    postRVAdapter.notifyDataSetChanged()

                    // 새로고침 상태를 false로 변경해서 새로고침 완료
                    binding.homeSwipeRefresh.isRefreshing = false
                } else {
                    Log.e("postService onResponse", "But not success")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("postService", "Failure message: ${t.message}")
            }
        })
    }

    fun loadMemberList() {
        val memberListService = retrofitBearer.create(MemberInterface::class.java)

        memberListService.memberList().enqueue(object : Callback<MemberResponse> {
            override fun onResponse(
                call: Call<MemberResponse>,
                response: Response<MemberResponse>
            ) {
                Log.e("memberService response code : ", "${response.code()}")
                Log.e("memberService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val memberData = response.body()?.result

                    memberData?.let {
                        val me = it.me
                        val leader = it.family_leader
                        val family = it.family
                        val wait = it.waitlist

                        // 내 sns id 저장
                        saveMyId(requireContext(), me.snsId)

                        binding.homeMenuTitleTv.text = memberData.family_name
                        binding.homeMenuCodeNumTv.text = memberData.family_code

                        binding.homeMenuCopyBt.setOnClickListener {
                            // 가족 코드를 클립보드에 복사
                            val familyCode = binding.homeMenuCodeNumTv.text.toString()
                            val clipboardManager =
                                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("Family Code", familyCode)
                            clipboardManager.setPrimaryClip(clipData)

                            // 복사 완료 메시지
                            val context = requireContext()
                            AnddeulToast.createToast(context, "가족 코드가 복사되었습니다").show()

                        }

                        // 가족 구성원 정보 리스트
                        val familyList = family.map { member ->
                            Member(member.snsId, member.nickname, member.image)
                        }

                        // 수락 요청 멤버 리스트
                        val waitList = wait.map { waitMember ->
                            Member(waitMember.snsId, waitMember.nickname, waitMember.image)
                        }

                        settingMyData(me, leader)
                        settingFamilyList(familyList, leader)
                        settingWaitList(waitList)
                    }
                } else {
                    Log.e("memberService", "멤버 데이터 로드 실패")
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                context?.let { AnddeulErrorToast.createToast(it, "서버 연결이 불안정합니다").show() }
                Log.e("memberService", "Failure message: ${t.message}")
            }
        })
    }

    // 유저 프로필로 이동
    fun changeUserProfile(userId: String) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.home_drawer_layout, UserProfileFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val idJson = gson.toJson(userId)
                    putString("selectedId", idJson)
                }
            })
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getExtensionVersion(Build.VERSION_CODES.R) >= 2
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

    override fun onAcceptClicked() {
        loadMemberList()
    }

    override fun onCancelClicked() {}

    @SuppressLint("IntentReset")
    fun startAlbumLauncher() {
        val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // 이미지 여러개 선택 가능
        albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        // 액티비티를 실행한다.
        albumLauncher.launch(albumIntent)
    }

    fun startPhotoPicker() {
        pickMultipleMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun settingFamilyList(familyList: List<Member>, leader: String) {

        val memberLayout = binding.homeMenuMembersLinear
        memberLayout.removeAllViews() // 기존 뷰들을 모두 제거

        for (memberData in familyList) {
            val memberBinding = FragmentHomeMenuMemberBinding.inflate(
                LayoutInflater.from(context),
                memberLayout,
                true
            )

            with(memberBinding) {
                homeMenuMemberNameTv.text = memberData.nickname

                Glide.with(homeMenuMemberProfileIv.context)
                    .load(memberData.image)
                    .error(R.drawable.default_profile)
                    .into(homeMenuMemberProfileIv)

                if (leader == memberData.nickname) {
                    homeMenuMemberLeaderIv.visibility = View.VISIBLE
                } else {
                    homeMenuMemberLeaderIv.visibility = View.GONE
                }

                // 멤버 프로필 사진 클릭 시 유저 프로필로 이동
                homeMenuMemberProfileIv.setOnClickListener {
                    drawerLayout.closeDrawers()
                    changeUserProfile(memberData.snsId)
                }

                // 멤버 이름 클릭 시 유저 프로필로 이동
                homeMenuMemberNameTv.setOnClickListener {
                    drawerLayout.closeDrawers()
                    changeUserProfile(memberData.snsId)
                }

                homeMenuMemberCheckBtn.setOnClickListener {
                    // drawerLayout 자동 닫기
                    drawerLayout.closeDrawers()

                    // 체크리스트 화면으로 이동
                    val intent = Intent(context, AddChecklistActivity::class.java)
                    intent.putExtra("checkUserId", memberData.snsId)
                    intent.putExtra("checkUserName", memberData.nickname)
                    startActivity(intent)
                }
            }
        }
    }

    fun settingWaitList(waitList: List<Member>) {
        val waitLayout = binding.homeMenuRequestMembersLinear
        waitLayout.removeAllViews() // 기존 뷰들을 모두 제거

        for (waitData in waitList) {
            val waitBinding = FragmentHomeMenuRequestMemberBinding.inflate(
                LayoutInflater.from(context),
                waitLayout, true
            )

            waitBinding.homeMenuRequestMemberNameTv.text = waitData.nickname

            // 수락하기 버튼 클릭시 (api 연결하기)
            waitBinding.homeMenuRequestAcceptBt.setOnClickListener {
                val dialog = ConfirmDialog(
                    waitData.nickname,
                    "그룹 이름",
                    waitData.snsId,
                    this@HomeFragment
                )
                dialog.isCancelable = false
                dialog.show(parentFragmentManager, "home accept confirm dialog")
            }

            // 수락 요청 멤버 프로필 사진 설정
            val profileImageUrl = waitData.image
            val imageView = waitBinding.homeMenuRequestMemberProfileIv
            val loadImage = LoadProfileImage(imageView)
            loadImage.execute(profileImageUrl)
        }
    }

    fun settingMyData(me: Member, leader: String) {
        // 내 프로필 이름 설정
        binding.homeMenuMyProfileNameIv.text = me.nickname

        Glide.with(binding.homeMenuMyProfileIv.context)
            .load(me.image)
            .error(R.drawable.default_profile)
            .into(binding.homeMenuMyProfileIv)

        // 내 프로필 사진, 이름 클릭 시 클릭 막기
        binding.homeMenuMyProfileNameIv.setOnClickListener {}
        binding.homeMenuMyProfileIv.setOnClickListener {}

        if (leader == me.nickname) {
            binding.homeMenuMyProfileLeaderIv.visibility = View.VISIBLE
        } else {
            binding.homeMenuMyProfileLeaderIv.visibility = View.GONE
        }
    }
}