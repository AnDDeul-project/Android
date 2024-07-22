package com.umc.anddeul.checklist.model

data class CompleteRoot (
    val status : Int,
    val isSuccess : Boolean,
    val check : CompleteCheck
)

data class CompleteCheck (
    val checkid : Int,
    val due_date : String,
    val complete : Int
)