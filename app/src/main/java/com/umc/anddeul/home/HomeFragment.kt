package com.umc.anddeul.home

import PostsInterface
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.common.AnddeulToast
import com.umc.anddeul.databinding.FragmentHomeBinding
import com.umc.anddeul.databinding.FragmentHomeMenuMemberBinding
import com.umc.anddeul.databinding.FragmentHomeMenuRequestMemberBinding
import com.umc.anddeul.home.model.Member
import com.umc.anddeul.home.model.MemberResponse
import com.umc.anddeul.home.model.Post
import com.umc.anddeul.home.model.PostData
import com.umc.anddeul.home.network.MemberInterface
import com.umc.anddeul.home.service.PostService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(), ConfirmDialogListener {
    lateinit var binding: FragmentHomeBinding
    private var postService = context?.let { PostService(it) }
    lateinit var postRVAdapter: PostRVAdapter
    lateinit var drawerLayout: DrawerLayout

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        postRVAdapter = PostRVAdapter(requireContext(), listOf(), listOf()) // 어댑터와 postDatas 연결
        binding.homeFeedRv.adapter = postRVAdapter // recyclerView에 Adapter 연결
        binding.homeFeedRv.layoutManager = LinearLayoutManager(context)

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = binding.homeDrawerLayout

        // 메뉴 가족 구성원 정보 가져오기
        loadMemberList()

        binding.homeToolbarMenuIb.setOnClickListener {
            Log.e("toolbar", "click!!!!!!!!")
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
            Log.d("getPost", "swipe")
            // getPostData()
            loadPost()
        }

        // Floating Action Button 클릭 시
        binding.homeFloatingBt.setOnClickListener {
            checkPermission()
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

            override fun onModifyClick(postId: Int, selectedImages: List<String>, postContent: String) {
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


//    fun getPostData() {
//        Log.e("getPost", "call")
//
//        postService?.getPost { post ->
//            if (post != null) {
//                if (post.isSuccess) { // 응답 코드 200 (연결 성공)
//                    Log.e("postData", "${post.result}")
//                } else {
//                    Log.e("postData", "${post.code}")
//                }
//            }
//
//        }
//    }

    fun saveMyId(context: Context, myId: String) {
        val spfMyId = context.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val editor = spfMyId.edit()
        editor.putString("myId", myId)
        editor.apply()
    }

    fun loadPost() {

        // 저장된 sns id 리스트 가져오기
        val spfSnsId = requireActivity().getSharedPreferences("saveSnsId", Context.MODE_PRIVATE)
        val size = spfSnsId.all.size
        val snsIds = (0 until size).mapNotNull {
            val snsId = spfSnsId.getString("snsId_$it", "not found")
            if (snsId != "not found") snsId else null
        }

        // 내 sns id 가져오기
        val spfMyId = requireActivity().getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
        val myId = spfMyId.getString("myId", "not found")

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

        val postService = retrofitBearer.create(PostsInterface::class.java)

        postService.homePosts().enqueue(object : Callback<Post> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.e("postService", "onResponse")
                Log.e("postService response code : ", "${response.code()}")
                Log.e("postService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val postData = response.body()?.result?.map {
                        PostData(it.post_idx, it.user_idx, it.nickname, it.content, it.picture, it.userImage)
                    }
                    Log.e("postService", "$postData")
                    if (postData != null) {

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
                    }

                    // 새로고침 상태를 false로 변경해서 새로고침 완료
                    binding.homeSwipeRefresh.isRefreshing = false
                } else {
                    Log.e("postService onResponse", "But not success")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("postService", "onFailure")
                Log.e("postService", "Failure message: ${t.message}")
            }
        })

    }


    fun loadMemberList() {
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

        val memberListService = retrofitBearer.create(MemberInterface::class.java)

        memberListService.memberList().enqueue(object : Callback<MemberResponse> {
            override fun onResponse(
                call: Call<MemberResponse>,
                response: Response<MemberResponse>
            ) {
                Log.e("memberService", "onResponse")
                Log.e("memberService response code : ", "${response.code()}")
                Log.e("memberService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val memberData = response.body()?.result

                    memberData?.let {
                        val me = it.me
                        val family = it.family
                        val wait = it.waitlist

                        // 가족 초대 코드 설정
                        binding.homeMenuCodeNumTv.text = memberData.family_code

                        binding.homeMenuCopyBt.setOnClickListener {
                            // 가족 코드를 클립보드에 복사
                            val familyCode = binding.homeMenuCodeNumTv.text.toString()
                            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("Family Code", familyCode)
                            clipboardManager.setPrimaryClip(clipData)

                            // 복사 완료 메시지
                            val context = requireContext()
                            AnddeulToast.createToast(context, "가족 코드가 복사되었습니다")?.show()

                        }

                        // 내 프로필 이름 설정
                        binding.homeMenuMyProfileNameIv.text = me.nickname
                        // 내 프로필 사진 설정
                        val profileImageUrl = me.image
                        val imageView = binding.homeMenuMyProfileIv
                        val loadImage = LoadProfileImage(imageView)
                        loadImage.execute(profileImageUrl)

                        // 가족 구성원 정보 리스트
                        val familyList = family.map { member ->
                            Member(member.snsId, member.nickname, member.image)
                        }

                        val memberLayout = binding.homeMenuMembersLinear
                        memberLayout.removeAllViews() // 기존 뷰들을 모두 제거

                        for (memberData in familyList) {
                            val memberBinding = FragmentHomeMenuMemberBinding.inflate(
                                LayoutInflater.from(context),
                                memberLayout,
                                true
                            )
                            memberBinding.homeMenuMemberNameTv.text = memberData.nickname

                            // 가족 구성원 프로필 사진 설정
                            val profileImageUrl = memberData.image
                            val imageView = memberBinding.homeMenuMemberProfileIv
                            val loadImage = LoadProfileImage(imageView)
                            loadImage.execute(profileImageUrl)

                            // 멤버 프로필 사진 클릭 시 유저 프로필로 이동
                            memberBinding.homeMenuMemberProfileIv.setOnClickListener {
                                // drawerLayout 자동 닫기
                                drawerLayout.closeDrawers()
                                changeUserProfile(memberData.snsId)
                            }

                            // 멤버 이름 클릭 시 유저 프로필로 이동
                            memberBinding.homeMenuMemberNameTv.setOnClickListener {
                                // drawerLayout 자동 닫기
                                drawerLayout.closeDrawers()
                                changeUserProfile(memberData.snsId)
                            }
                        }

                        // 내 sns id 저장
                        saveMyId(requireContext(), me.snsId)

                        // 수락 요청 멤버 리스트
                        val waitList = wait.map { waitMember ->
                            Member(waitMember.snsId, waitMember.nickname, waitMember.image)
                        }

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
                                val dialog = ConfirmDialog(waitData.nickname, "행복한 우리 가족", waitData.snsId, this@HomeFragment)
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
                } else {
                    Log.e("memberService", "멤버 데이터 로드 실패")
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Log.e("memberService", "onFailure")
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

    override fun onAcceptClicked() {
        loadMemberList()
    }

    override fun onCancelClicked() { }
}