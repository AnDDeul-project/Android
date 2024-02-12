package com.umc.anddeul.home.model

data class UserProfileDTO(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: UserProfileData
)

data class UserProfileData(
    val nickname : String,
    val image : String,
    val postCount : Int,
    val firstPostImages : List<String>?,
    val postIdx : List<Int>
)