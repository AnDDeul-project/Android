package com.umc.anddeul.home.model

data class LogoutDTO(
    val status : Int,
    val isSuccess : Boolean,
    val accesstoken : String
)
