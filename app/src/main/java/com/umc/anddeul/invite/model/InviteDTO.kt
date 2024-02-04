package com.umc.anddeul.invite.model

import com.google.gson.annotations.SerializedName

// 가족 코드 랜덤 생성
data class NewFamilyCode (
    val status: Int,
    val isSuccess: Boolean,
    val randomToken: String?,
    val error: String?,
    val message: String?,
)

// 가족 참여 요청 코드
data class AddFamilyCode (
    val status: Int,
    val isSuccess: Boolean,
    @SerializedName("family_code") val familyCode: String?,
    val error: String?,
    val message: String?,

)

// 가족 검색 정보
data class FamilyInfoResponse(
    val status: Int,
    val isSuccess: Boolean,
    @SerializedName("family_name") val familyName: String,
    @SerializedName("family_image") val familyImages: List<FamilyImage>,
    @SerializedName("family_count") val familyCount: Int
)

data class FamilyImage(
    @SerializedName("image") val image: String
)