package com.umc.anddeul.postbox.model

data class QuestionResponse(
    val status: Long,
    val isSuccess: Boolean,
    val question: List<Question>,
)

data class Question(
    val content: String,
)
