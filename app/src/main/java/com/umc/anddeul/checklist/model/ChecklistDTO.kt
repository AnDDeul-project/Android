package com.umc.anddeul.checklist.model

data class Root (
    val checklist : List<Checklist>
)

data class Checklist (
    val check_idx : Int,
    val complete : Int,
    val picture : String,
    val content : String,
    val sender : String,
    val receiver : String
)