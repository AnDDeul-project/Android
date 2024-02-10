package com.umc.anddeul.mypage

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umc.anddeul.MainActivity
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypageBinding
import com.umc.anddeul.home.PermissionDialog
import com.umc.anddeul.home.PostUploadActivity

class MyPageFragment : Fragment() {
    lateinit var binding: FragmentMypageBinding
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

        // 게시글 더미
        val myFeed = listOf("1", "2", "3", "4", "5")

        val mypageProfileRVAdapter = MyPageProfileRVAdapter(myFeed)
        binding.mypageProfileRv.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.mypageProfileRv.adapter = mypageProfileRVAdapter

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
}