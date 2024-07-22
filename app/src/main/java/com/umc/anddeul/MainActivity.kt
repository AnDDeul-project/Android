package com.umc.anddeul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.checklist.ChecklistRVAdapter
import com.umc.anddeul.checklist.service.ChecklistAlarmService
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager
import com.umc.anddeul.databinding.ActivityMainBinding
import com.umc.anddeul.home.HomeFragment
import com.umc.anddeul.home.service.HomeAlarmService
import com.umc.anddeul.mypage.MyPageFragment
import com.umc.anddeul.mypage.MyPageViewModel
import com.umc.anddeul.postbox.PostboxFragment
import com.umc.anddeul.postbox.service.PostboxAlarmService
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val REQUEST_IMAGE_CAPTURE = 200
    val checklistRVAdapter = ChecklistRVAdapter(this)

    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initBottomNavigation() // bottom navigation 설정

//        TokenManager.initialize(this) // 토큰 매니저 초기화
//        TokenManager.setToken("각자 토큰")
//
//        RetrofitManager.initialize("https://umc-garden.store") // RetrofitManager 초기화

//        // FCM 토큰 발급
//        val TAG = "seeToken"
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val token = task.result
//                // Use the token as needed
//                Log.d(TAG, token)
//            } else {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//
        // 가족 우체통 하단바 알림
        postboxBottomAlarm()
        // 홈 하단바 알림
        homeBottomAlarm()
        // 체크리스트 하단바 알림
        checklistBottomAlarm()
//            }
//        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                checklistBottomAlarm()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lateinit var file : File

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    val checklistFragment = ChecklistFragment()
                    Log.d("확인","체크리스트: $, 파일: ${checklistFragment.checklistRVAdapter?.file}")
                }
            }
        }
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
                    homeBottomAlarm()   // 홈 하단바 알림
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // ChecklistFragment로 이동
                R.id.checklistFragment -> {
                    homeBottomAlarm()   // 홈 하단바 알림
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ChecklistFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // postboxFragment로 이동
                R.id.postboxFragment -> {
                    homeBottomAlarm()   // 홈 하단바 알림
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PostboxFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // myPageFragment로 이동
                R.id.myPageFragment -> {
                    homeBottomAlarm()   // 홈 하단바 알림
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MyPageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    // 토큰 불러오기
    private fun loadJwt(): String {
        val spf = getSharedPreferences("myToken", MODE_PRIVATE)
        return spf.getString("jwtToken", null).toString()
    }

    // 홈 하단바 알림
    private fun homeBottomAlarm() {
        val loadedToken = loadJwt() // jwt토큰
        // api 연결
        val homeAlarmService = HomeAlarmService()
        homeAlarmService.alarmHome(loadedToken) { homeDTO ->
            if (homeDTO != null) {
                if (homeDTO.isSuccess.toString() == "true") {
                    if (homeDTO?.count!! <= 0) {         // 알림 0개
                        binding.homeCircle.visibility = View.GONE
                        binding.homeCnt.visibility = View.GONE
                    } else {
                        binding.homeCircle.visibility = View.VISIBLE
                        binding.homeCnt.visibility = View.VISIBLE
                        if (homeDTO?.count!! <= 9){         // 알림 1자리 수
                            binding.homeCnt.text = homeDTO?.count.toString()
                            binding.homeCircle.setImageResource(R.drawable.img_alarm_circle)
                        } else if (homeDTO?.count!! <= 99) {         // 알림 2자리 수
                            binding.homeCnt.text = homeDTO?.count.toString()
                            binding.homeCircle.setImageResource(R.drawable.img_alarm_circle2)
                        }else if (homeDTO?.count!! <= 999) {         // 알림 3자리 수
                            binding.homeCnt.text = homeDTO?.count.toString()
                            binding.homeCircle.setImageResource(R.drawable.img_alarm_circle3)
                        } else {         // 알림 4자리 수 이상
                            binding.homeCnt.text = "999"
                            binding.homeCircle.setImageResource(R.drawable.img_alarm_circle3)
                        }
                    }
                }
            }
        }
    }

    // 가족 우체통 하단바 알림
    private fun postboxBottomAlarm() {
        val loadedToken = loadJwt() // jwt토큰
        // api 연결
        val postboxAlarmService = PostboxAlarmService()
        postboxAlarmService.alarmPostbox(loadedToken) { postboxDTO ->
            if (postboxDTO != null) {
                if (postboxDTO.isSuccess.toString() == "true") {
                    if (postboxDTO?.count!! <= 0) {         // 알림 0개
                        binding.postboxCircle.visibility = View.GONE
                        binding.postboxCnt.visibility = View.GONE
                    } else {
                        binding.postboxCircle.visibility = View.VISIBLE
                        binding.postboxCnt.visibility = View.VISIBLE
                        if (postboxDTO?.count!! <= 9){         // 알림 1자리 수
                            binding.postboxCnt.text = postboxDTO?.count.toString()
                            binding.postboxCircle.setImageResource(R.drawable.img_alarm_circle)
                        } else if (postboxDTO?.count!! <= 99) {         // 알림 2자리 수
                            binding.postboxCnt.text = postboxDTO?.count.toString()
                            binding.postboxCircle.setImageResource(R.drawable.img_alarm_circle2)
                        }else if (postboxDTO?.count!! <= 999) {         // 알림 3자리 수
                            binding.postboxCnt.text = postboxDTO?.count.toString()
                            binding.postboxCircle.setImageResource(R.drawable.img_alarm_circle3)
                        } else {         // 알림 4자리 수 이상
                            binding.postboxCnt.text = "999"
                            binding.postboxCircle.setImageResource(R.drawable.img_alarm_circle3)
                        }
                    }
                }
            }
        }
    }

    private fun checklistBottomAlarm() {
        val loadedToken = loadJwt() // jwt토큰
        // api 연결
        val checklistAlarmService = ChecklistAlarmService()
        checklistAlarmService.alarmApi { checklistAlarmDTO ->
            if (checklistAlarmDTO != null) {
                if (checklistAlarmDTO.isSuccess.toString() == "true") {
                    if (checklistAlarmDTO?.count!! <= 0) {         // 알림 0개
                        binding.checklistCnt.visibility = View.GONE
                        binding.checklistCircle.visibility = View.GONE
                    } else {
                        binding.checklistCnt.visibility = View.VISIBLE
                        binding.checklistCircle.visibility = View.VISIBLE
                        if (checklistAlarmDTO?.count!! <= 9) {         // 알림 1자리 수
                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle)
                        } else if (checklistAlarmDTO?.count!! <= 99) {         // 알림 2자리 수
                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle2)
                        } else if (checklistAlarmDTO?.count!! <= 999) {         // 알림 3자리 수
                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle3)
                        } else {         // 알림 4자리 수 이상
                            binding.checklistCnt.text = "999"
                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle3)
                        }
                    }
                }
            }
        }
    }
}