package com.umc.anddeul.postbox.model

import com.google.gson.annotations.SerializedName

data class FamilyResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: FamilyData
)

data class FamilyData(
    val me : Family,
    @SerializedName("family_code") val familyCode : String,
    val family : List<Family>,
    val waitlist : List<Family>
)

data class Family (
    val snsId : String,
    val nickname: String,
    val image : String
)