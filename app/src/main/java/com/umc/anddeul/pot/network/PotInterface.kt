package com.umc.anddeul.pot.network
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.LoveRoot
import com.umc.anddeul.pot.model.PointRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PotInterface {
    @GET("/garden/flower")
    fun getFlower() : Call<FlowerRoot>

    @PUT("/garden/flower/givelove")
    fun giveLove() : Call<LoveRoot>

    @GET("/garden/flower/mypoint")
    fun getMyPoint() : Call<PointRoot>

    @GET("/garden/flower/{flowerid}")
    fun getFlowerId(
        @Path("flowerid") flowerid : String
    )
}