package com.umc.anddeul.pot.model

import retrofit2.Call

data class PointRoot (
    val point : Point
)

data class Point (
    val point : Int
)

data class LoveRoot (
    val result : Result
)

data class Result (
    val point : Int,
    val img : ResultImg
)

data class ResultImg (
    val changed_img : String
)

data class FlowerRoot (
    val flower : Flower
)

data class Flower (
    val idx : Int,
    val point : Int,
    val name : String,
    val img : String
)