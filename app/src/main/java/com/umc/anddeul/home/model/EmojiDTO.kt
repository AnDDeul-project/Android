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
    val happy_emj: List<String>?,
    val laugh_emj: List<String>?,
    val sad_emj: List<String>?
)
