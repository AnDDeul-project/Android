package com.umc.anddeul.mypage

import androidx.lifecycle.ViewModel
import com.umc.anddeul.home.model.UserProfileData

class MyPageViewModel : ViewModel() {
    private var myProfileData: UserProfileData? = null

    fun setMyProfile(myProfile: UserProfileData) {
        myProfileData = myProfile
    }

    fun getMyProfile(): UserProfileData? {
        return myProfileData
    }
}