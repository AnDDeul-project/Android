package com.umc.anddeul.data.signin

data class SigninResponse (
    val status: Int,
    val isSuccess: Boolean,
    val accessToken: String?
)