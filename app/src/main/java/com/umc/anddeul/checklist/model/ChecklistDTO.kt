package com.umc.anddeul.checklist.model

data class Root (
    val checklist : List<Checklist>
)

data class Checklist (
    val check_id : Int,
    val complete : Int,
    val picture : String,
    val content : String,
    val alarm_idx : Int,
    val sender : String,
)