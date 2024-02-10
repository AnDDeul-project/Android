package com.umc.anddeul.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.umc.anddeul.home.model.UserProfileData

class MyPageViewModel : ViewModel() {
    private val myProfileData = MutableLiveData<UserProfileData>()

    fun setMyProfile(myProfile : UserProfileData) {
        myProfileData.value = myProfile
    }

    fun getMyProfile() : LiveData<UserProfileData> {
        return myProfileData
    }
}