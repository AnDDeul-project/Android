package com.umc.anddeul.postbox.model

import okhttp3.MultipartBody

data class VoiceRequest (
    val member : String,
    val question : String,
    val record: MultipartBody.Part
)

data class VoiceResponse (
    val status: Int,
    val isSuccess: Boolean,
    val message: String
)