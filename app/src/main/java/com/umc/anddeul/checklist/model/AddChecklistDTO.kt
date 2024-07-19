package com.umc.anddeul.checklist.model

data class AddChecklist (
    var receiver_idx : String,
    var due_date : String,
    var content : String
)

data class AddRoot (
    var success : Boolean,
    var check : Check
)

data class Check (
    val check_idx : Int,
    val due_date : String?,
    val complete : Int,
    val picture : String,
    val content : String,
    val sender : String,
    val receiver : String,
    var picturePath: String? = null
)