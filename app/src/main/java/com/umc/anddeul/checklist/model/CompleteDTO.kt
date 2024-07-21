package com.umc.anddeul.checklist.model

data class CompleteRoot (
    val isSuccess : Boolean,
    val check : CompleteCheck
)

data class CompleteCheck (
    val checkid : Int,
    val dueDate : String,
    val complete : Int
)