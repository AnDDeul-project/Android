package com.umc.anddeul.home.model

import okhttp3.MultipartBody

data class BoardRequest(
    val content : String,
    val image : MultipartBody.Part
)

data class BoardResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)