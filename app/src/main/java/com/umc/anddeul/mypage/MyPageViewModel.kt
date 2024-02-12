package com.umc.anddeul.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.umc.anddeul.home.model.UserProfileData

class MyPageViewModel : ViewModel() {
    private val myProfileData = MutableLiveData<UserProfileData>()

    fun setMyProfile(myProfile : UserProfileData) {
        myProfileData.value = myProfile
        Log.e("setMyProfile", "${myProfileData.value}")
    }

    fun getMyProfile() : LiveData<UserProfileData> {
        return myProfileData
        Log.e("setMyProfile", " getMyProfile 호출 : ${myProfileData.value}")

    }
}