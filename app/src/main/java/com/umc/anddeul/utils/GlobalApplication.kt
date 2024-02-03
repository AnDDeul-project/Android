package com.umc.anddeul.utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.umc.anddeul.R

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}