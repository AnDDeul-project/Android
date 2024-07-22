package com.umc.anddeul.utils

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.umc.anddeul.BuildConfig
import com.umc.anddeul.common.RetrofitManager
import com.umc.anddeul.common.TokenManager

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        TokenManager.initialize(this)
        RetrofitManager.initialize("https://umc-garden.store")

        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

//        // 키 해시 확인
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("키 해시 확인", keyHash)
    }
}