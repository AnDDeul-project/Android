package com.umc.anddeul.utils

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.umc.anddeul.BuildConfig
import com.umc.anddeul.R

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

//        // 키 해시 확인
//        var keyHash = Utility.getKeyHash(this)
//        Log.d("키 해시 확인", keyHash)
    }
}