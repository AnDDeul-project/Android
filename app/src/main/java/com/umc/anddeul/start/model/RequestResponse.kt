package com.umc.anddeul.start.model

data class RequestResponse (
    val status: Int,
    val isSuccess: Boolean,
    val infamily: Boolean,
    val request: Boolean,
)