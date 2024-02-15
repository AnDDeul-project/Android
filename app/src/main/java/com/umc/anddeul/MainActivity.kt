package com.umc.anddeul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.databinding.ActivityMainBinding
import com.umc.anddeul.home.HomeFragment
import com.umc.anddeul.mypage.MyPageFragment
import com.umc.anddeul.mypage.MyPageViewModel
import com.umc.anddeul.postbox.PostboxFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val REQUEST_IMAGE_CAPTURE = 200

    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initBottomNavigation() // bottom navigation 설정
    }

    private fun initBottomNavigation() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        // bottom navigation 하단 탭 이동
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {

                // HomeFragment로 이동
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // ChecklistFragment로 이동
                R.id.checklistFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ChecklistFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // postboxFragment로 이동
                R.id.postboxFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PostboxFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // myPageFragment로 이동
                R.id.myPageFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MyPageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 프래그먼트로 결과 전달
        supportFragmentManager.fragments.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}