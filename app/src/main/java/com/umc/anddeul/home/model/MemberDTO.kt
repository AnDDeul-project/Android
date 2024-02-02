package com.umc.anddeul.home.model

data class MemberResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: MembersData
)

data class MembersData(
    val me : Member,
    val family_code : String,
    val family : List<Member>,
    val waitlist : List<Member>
)

data class Member (
    val snsId : String,
    val nickname: String,
    val image : String
)