package com.umc.anddeul.pot.network
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PotInterface {
    @GET("/garden/flower")
    fun getFlower()

    @PUT("/garden/flower/givelove")
    fun giveLove()

    @GET("/garden/flower/mypoint")
    fun getMyPoint()

    @GET("/garden/flower/{flowerid}")
    fun getFlowerId(
        @Path("flowerid") flowerid : String
    )
}