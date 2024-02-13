package com.umc.anddeul.checklist.model

data class AddChecklist (
    var receiver_idx : String,
    var due_year : Int,
    var due_month : Int,
    var due_day : Int,
    var content : String
)