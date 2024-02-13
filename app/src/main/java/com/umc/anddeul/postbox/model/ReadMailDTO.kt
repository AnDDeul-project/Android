package com.umc.anddeul.postbox.model

import com.google.gson.annotations.SerializedName

data class ReadMailResponse (
    val status: Long,
    val isSuccess: Boolean,
    val post: List<Letter>,
)

data class Letter(
    @SerializedName("postbox_idx") val postboxIdx: Long,
    @SerializedName("sender_idx") val senderIdx: String,
    @SerializedName("receiver_idx") val receiverIdx: String,
    val content: String,
    val voice: Long,
    @SerializedName("send_date") val sendDate: String,
    @SerializedName("is_read") val isRead: Long,
    val question: Any?,
)
