package com.umc.anddeul.home.model

data class OnePostDTO(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: OnePostData
)

data class OnePostData(
    val post_idx: Int,
    val content: String,
    val picture: List<String>,
    val create_at: String,
    val nickname: String,
    val userImage: String
)
