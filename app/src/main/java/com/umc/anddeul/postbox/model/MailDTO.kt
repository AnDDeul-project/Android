package com.umc.anddeul.postbox.model

import com.google.gson.annotations.SerializedName

// 오늘 편지 받기
data class TodayMailResponse(
    val status: Long,
    val isSuccess: Boolean,
    val post: List<Post>,
)

data class Post(
    @SerializedName("postbox_idx") val postboxIdx: Long,
    @SerializedName("sender_idx") val senderIdx: String,
    val voice: Long,
    val content: String?,
    @SerializedName("is_read") val isRead: Long,
)
