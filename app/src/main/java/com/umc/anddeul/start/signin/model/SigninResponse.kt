package com.umc.anddeul.start.signin.model

data class SigninResponse (
    val status: Int,
    val isSuccess: Boolean,
    val accessToken: String?,
    val has: Boolean?,
    val message: String?,

)