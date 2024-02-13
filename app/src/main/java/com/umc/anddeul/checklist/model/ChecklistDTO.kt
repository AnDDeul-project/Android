package com.umc.anddeul.checklist.model

data class Root (
    val result : List<Result>
)

data class Result (
    val checkid : Int,
    val sender : String,
    val complete : Int,
    val picture : String,
    val content : String
)