package com.umc.anddeul

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.umc.anddeul.checklist.ChecklistFragment
import com.umc.anddeul.checklist.service.ChecklistAlarmService
import com.umc.anddeul.databinding.ActivityMainBinding
import com.umc.anddeul.home.HomeFragment
import com.umc.anddeul.mypage.MyPageFragment
import com.umc.anddeul.postbox.PostboxFragment
import com.umc.anddeul.postbox.service.PostboxAlarmService

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation() // bottom navigation 설정

        // 가족 우체통 하단바 알림
        postboxBottomAlarm()
        // 체크리스트 하단바 알림
        //checklistBottomAlarm()
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                //checklistBottomAlarm()
            }
        }
        return super.dispatchTouchEvent(ev)
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
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // ChecklistFragment로 이동
                R.id.checklistFragment -> {
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ChecklistFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // postboxFragment로 이동
                R.id.postboxFragment -> {
                    postboxBottomAlarm()    // 가족 우체통 하단바 알림
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, PostboxFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                // myPageFragment로 이동
                R.id.myPageFragment -> {
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

//    private fun checklistBottomAlarm() {
//        val loadedToken = loadJwt() // jwt토큰
//        // api 연결
//        val checklistAlarmService = ChecklistAlarmService()
//        checklistAlarmService.alarmApi { checklistAlarmDTO ->
//            if (checklistAlarmDTO != null) {
//                if (checklistAlarmDTO.isSuccess.toString() == "true") {
//                    if (checklistAlarmDTO?.count!! <= 0) {         // 알림 0개
//                        binding.checklistCnt.visibility = View.GONE
//                        binding.checklistCircle.visibility = View.GONE
//                    } else {
//                        binding.checklistCnt.visibility = View.VISIBLE
//                        binding.checklistCircle.visibility = View.VISIBLE
//                        if (checklistAlarmDTO?.count!! <= 9) {         // 알림 1자리 수
//                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
//                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle)
//                        } else if (checklistAlarmDTO?.count!! <= 99) {         // 알림 2자리 수
//                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
//                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle2)
//                        } else if (checklistAlarmDTO?.count!! <= 999) {         // 알림 3자리 수
//                            binding.checklistCnt.text = checklistAlarmDTO?.count.toString()
//                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle3)
//                        } else {         // 알림 4자리 수 이상
//                            binding.checklistCnt.text = "999"
//                            binding.checklistCircle.setImageResource(R.drawable.img_alarm_circle3)
//                        }
//                    }
//                }
//            }
//        }
//    }
}