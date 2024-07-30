package com.umc.anddeul.home.model

data class ModifyRequest (
    val content: String
)

data class PostModifyDTO (
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)