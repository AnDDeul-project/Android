package com.umc.anddeul.pot.model

import retrofit2.Call

data class Root (
    var point : List<Point>
)

data class Point (
    var point : Int
)