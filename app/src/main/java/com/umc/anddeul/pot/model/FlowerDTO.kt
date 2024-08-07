package com.umc.anddeul.pot.model

data class PointRoot (
    val point : Point
)

data class Point (
    val point : Int
)

data class LoveRoot (
    val status: Int,
    val isSuccess: Boolean,
    val result : Result
)

data class Result (
    val point : Int,
    val changed_img : List<ChangedImg>
)

data class ChangedImg (
    val img : String?,
    val gauge : String?
)

data class FlowerRoot (
    val flower : Flower
)

data class Flower (
    val idx : Int,
    val point : Int,
    val name : String,
    val img : String,
    val gauge : String
)

data class GardenRoot (
    val status : Int,
    val isSuccess: Boolean,
    val garden : Garden
)

data class Garden (
    val theme : String,
    val flowers : List<Flowers>
)

data class Flowers(
    val idx : String,
    val img_5 : String
)