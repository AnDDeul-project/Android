package com.umc.anddeul.mypage.model

data class LogoutDTO(
    val status : Int,
    val isSuccess : Boolean,
    val accesstoken : String
)
