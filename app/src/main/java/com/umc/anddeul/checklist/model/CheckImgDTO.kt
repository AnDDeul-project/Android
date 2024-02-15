package com.umc.anddeul.checklist.model

data class CheckImgRoot (
    var isSuccess : Boolean,
    var check : CheckImg
)

data class CheckImg (
    var checkid : Int,
    var picture : String
)
