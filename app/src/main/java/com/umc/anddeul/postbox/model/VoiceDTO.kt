package com.umc.anddeul.postbox.model

import okhttp3.MultipartBody

data class VoiceRequest (
    val member : List<Long>,
    val question : String,
    val record: MultipartBody.Part
)

data class VoiceResponse (
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)