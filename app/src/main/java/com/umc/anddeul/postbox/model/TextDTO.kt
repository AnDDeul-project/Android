package com.umc.anddeul.postbox.model

data class TextRequest (
    val member : String,
    val question : String,
    val content : String
)

data class TextResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)