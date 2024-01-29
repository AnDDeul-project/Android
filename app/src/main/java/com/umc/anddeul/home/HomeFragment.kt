package com.umc.anddeul.home

import android.content.Intent
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
import com.umc.anddeul.databinding.FragmentHomeBinding
import com.umc.anddeul.databinding.FragmentHomeMenuMemberBinding
import com.umc.anddeul.databinding.FragmentHomeMenuRequestMemberBinding
import com.umc.anddeul.invite.InviteStartActivity

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    private var postDatas = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        postDatas.apply { // 임시 데이터 더미
            add(Post(1, "user1", "안녕하세요 첫번째 피드글입니다", "사진", 1, 1))
            add(Post(2, "user2", "안녕하세요 두번째 피드글입니다", "사진", 2, 2))
        }

        val postRVAdapter = PostRVAdapter(requireContext(), postDatas) // 어댑터와 postDatas 연결
        binding.homeFeedRv.adapter = postRVAdapter // recyclerView에 Adapter 연결
        binding.homeFeedRv.layoutManager = LinearLayoutManager(context)

        // 커스텀 툴바 사용
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeTb)
        // 툴바 기본 타이틀 없애기
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout : DrawerLayout = binding.homeDrawerLayout

        binding.homeToolbarMenuIb.setOnClickListener {
            Log.e("toolbar", "click!!!!!!!!")
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END)
            } else {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        val memberLayout = binding.homeMenuMembersLinear

        // 더미 데이터 리스트
        val memberDataList = listOf("이솜솜", "김솜솜", "박솜솜", "최솜솜")

        for (memberData in memberDataList) {
            val memberBinding = FragmentHomeMenuMemberBinding.inflate(LayoutInflater.from(context), memberLayout, true)
            memberBinding.homeMenuMemberNameTv.text = memberData
        }

        val requestMemberLayout = binding.homeMenuRequestMembersLinear
        // 수락 요청 더미 데이터 리스트
        val requestMemberDataList = listOf("이솜솜", "김솜솜")
        for (requestMember in requestMemberDataList) {
            val memberBinding = FragmentHomeMenuRequestMemberBinding.inflate(LayoutInflater.from(context), requestMemberLayout, true)
            memberBinding.homeMenuRequestMemberNameTv.text = requestMember

            memberBinding.homeMenuRequestAcceptBt.setOnClickListener {
                val dialog = ConfirmDialog("이솜솜", "행복한 우리 가족")
                dialog.isCancelable = false
                dialog.show(parentFragmentManager, "home accept confirm dialog")
            }
        }


        // swipe refresh layout 초기화 (swipe 해서 피드 새로고침)
        binding.homeSwipeRefresh.setOnRefreshListener {

        }

        // Floating Action Button 클릭 시
        binding.homeFloatingBt.setOnClickListener {
            Log.e("floating button", "click!!!!!!!!")

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
//                    스토리지 읽기 권한이 허용이면 커스텀 앨범 띄워주기
//                    권한 있을 경우 : PERMISSION_GRANTED
//                    권한 없을 경우 : PERMISSION_DENIED
                    Log.e("floatingButton", "activity go")
                    val postUploadActivity = Intent(activity, PostUploadActivity::class.java)
                    startActivity(postUploadActivity)
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //권한을 명시적으로 거부한 경우 : ture
                    //다시 묻지 않음을 선택한 경우 : false
                    //다이얼로그를 띄워 권한 팝업을 허용해야 갤러리 접근이 가능하다는 사실을 알려줌
                    val permissionDialog = PermissionDialog()
                    permissionDialog.isCancelable = false
                    permissionDialog.show(parentFragmentManager, "permission dialog")
                }
            }

        }

        return binding.root
    }

    fun floatingButtonSetting() {

    }
}