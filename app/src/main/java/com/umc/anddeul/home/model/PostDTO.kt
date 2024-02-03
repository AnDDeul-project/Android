package com.umc.anddeul.home.model

data class Post(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: List<PostData>
)

data class PostData(
    val user_idx: String,
    val content: String,
    val picture: String
)
