package com.umc.anddeul.checklist.model

data class AddChecklist (
    var receiver_idx : String,
    var due_year : Int,
    var due_month : Int,
    var due_day : Int,
    var content : String
)

data class AddRoot (
    var success : Boolean,
    var check : List<Check>
)

data class Check (
    var checkid : Int,
    var due_date : String,
    var complete : Boolean,
    var picture : String,
    var content : String,
    var sender : String,
    var receiver : String
)