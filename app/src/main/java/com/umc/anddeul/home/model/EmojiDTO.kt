package com.umc.anddeul.home.model

data class EmojiRequest(
    var emojiType : String
)

data class EmojiDTO (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result : EmojiResult
)

data class EmojiResult(
    val postIdx: Int,
    val happyEmj: List<String>,
    val laughEmj: List<String>,
    val sadEmj: List<String>
)
