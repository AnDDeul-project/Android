package com.umc.anddeul.home

import PostsInterface
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
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

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var postService = context?.let { PostService(it) }
    lateinit var postRVAdapter: PostRVAdapter
    lateinit var drawerLayout : DrawerLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        postRVAdapter = PostRVAdapter(requireContext(), listOf()) // 어댑터와 postDatas 연결
        binding.homeFeedRv.adapter = postRVAdapter // recyclerView에 Adapter 연결
        binding.homeFeedRv.layoutManager = LinearLayoutManager(context)

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = binding.homeDrawerLayout


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
            Log.e("floating button", "click!!!!!!!!")

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
//                    스토리지 읽기 권한이 허용이면 커스텀 앨범 띄워주기
//                    권한 있을 경우 : PERMISSION_GRANTED
//                    권한 없을 경우 : PERMISSION_DENIED
                    Log.e("floatingButton", "activity go")
                    val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                    startActivity(postUploadActivity)
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                    //권한을 명시적으로 거부한 경우 : ture
                    //다시 묻지 않음을 선택한 경우 : false
                    //다이얼로그를 띄워 권한 팝업을 허용해야 갤러리 접근이 가능하다는 사실을 알려줌
                    val permissionDialog = PermissionDialog()
                    permissionDialog.isCancelable = false
                    permissionDialog.show(parentFragmentManager, "permission dialog")
                }
            }
        }


        postRVAdapter.setMyItemClickListener(object : PostRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int) {
                // 선택한 유저 프로필로 이동
                changeUserProfile(position)
            }
        })
        
        // 메뉴 가족 구성원 정보 가져오기
        loadMemberList()
        
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 게시글 조회
        loadPost()
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

    // 사용자의 snsId를 저장하는 함수
    fun saveSnsId(context: Context, snsId: List<String>) {
        val spfSnsId = context.getSharedPreferences("saveSnsId", Context.MODE_PRIVATE)
        val editor = spfSnsId.edit()
        snsId.forEachIndexed { index, id ->
            editor.putString("snsId_$index", id)
        }
        editor.apply()
    }

    fun loadPost() {

        val spf: SharedPreferences =
            requireActivity().getSharedPreferences("myToken", Context.MODE_PRIVATE)
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

        val postService = retrofitBearer.create(PostsInterface::class.java)

        postService.homePosts().enqueue(object : Callback<Post> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.e("postService", "onResponse")
                Log.e("postService response code : ", "${response.code()}")
                Log.e("postService response body : ", "${response.body()}")

                if (response.isSuccessful) {
                    val postData = response.body()?.result?.map {
                        PostData(it.user_idx, it.content, it.picture, it.userImage)
                    }
                    Log.e("postService", "$postData")
                    if (postData != null) {
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

                                (context as MainActivity).supportFragmentManager.beginTransaction()
                                    .add(R.id.home_drawer_layout, UserProfileFragment())
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss()
                            }
                        }

                        // snsId 저장
                        val allSnsIds = familyList.map { it.snsId } + listOf(me.snsId)
                        saveSnsId(requireContext(), allSnsIds)


                        // 수락 요청 멤버 리스트
                        val waitList = wait.map { waitMember ->
                            Member(waitMember.snsId, waitMember.nickname, waitMember.image)
                        }

                        val waitLayout = binding.homeMenuRequestMembersLinear

                        for (waitData in waitList) {
                            val waitBinding = FragmentHomeMenuRequestMemberBinding.inflate(
                                LayoutInflater.from(context),
                                waitLayout, true
                            )
                            waitBinding.homeMenuRequestMemberNameTv.text = waitData.nickname

                            // 수락하기 버튼 클릭시 (api 연결하기)
                            waitBinding.homeMenuRequestAcceptBt.setOnClickListener {
                                val dialog = ConfirmDialog(waitData.nickname, "행복한 우리 가족")
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
    fun changeUserProfile(position: Int) {
        // 저장된 sns id 리스트 가져오기
        val spfSnsId = requireActivity().getSharedPreferences("saveSnsId", Context.MODE_PRIVATE)
        val size = spfSnsId.all.size
        Log.e("selectedsnsId", "아이디 리스트 사이즈 : $size")
        val snsIds = (0 until size).mapNotNull {
            val snsId = spfSnsId.getString("snsId_$it", "not found")
            if (snsId != "not found") snsId else null
        }

        Log.e("userProfileService", "저장된 아이디 : ${snsIds}")

        // snsIds 리스트에서 position에 해당하는 인덱스 값 가져오기
        val selectedId = snsIds.getOrNull(position)

        (context as MainActivity).supportFragmentManager.beginTransaction()
            .add(R.id.home_drawer_layout, UserProfileFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val idJson = gson.toJson(selectedId)
                    putString("selectedId", idJson)
                }
            })
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}