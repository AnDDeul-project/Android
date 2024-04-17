package com.umc.anddeul.home.model

data class EmojiRequest(
    var emojiType : String
)

data class EmojiDTO (
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result : EmojiResponse
)

data class EmojiResponse(
    val emojis: Emojis
)

data class EmojiUiModel(
    val type: String,
    val selected: Boolean,
    val count: Int
)