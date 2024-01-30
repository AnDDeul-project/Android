package com.umc.anddeul.home

// home 화면 게시글
data class Post(
    var post_idx : Int?,
    var user_idx : String?,
    var content : String?,
    var picture : String?,
    var create_at : Int?,
    var modify_at : Int?

)
