package com.umc.anddeul.utils

import android.app.Application
import com.google.firebase.FirebaseApp
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.umc.anddeul.BuildConfig
import com.umc.anddeul.R

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

//        // 키 해시 확인
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("키 해시 확인", keyHash)
    }
}