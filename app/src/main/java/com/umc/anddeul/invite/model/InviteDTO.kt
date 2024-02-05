package com.umc.anddeul.invite.model

import com.google.gson.annotations.SerializedName

// 가족 코드 랜덤 생성 요청
data class NewFamilyRequest (
    @SerializedName("family_name") val familyName: String
)

// 가족 코드 랜덤 생성
data class NewFamilyResponse (
    @SerializedName("status") val status: Int,
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("randomToken") val randomToken: List<String>
)

// 가족 코드 참여
data class AddFamilyRequest (
    @SerializedName("family_code") val familyCode: String
)

// 가족 코드 참여 요청
data class AddFamilyResponse (
    val status: Int,
    val isSuccess: Boolean,
    @SerializedName("family_code") val familyCode: String
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