package com.umc.anddeul.pot.network
import com.umc.anddeul.pot.model.FlowerRoot
import com.umc.anddeul.pot.model.GardenRoot
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

    @GET("/garden/mypoint")
    fun getMyPoint() : Call<PointRoot>

    @GET("/garden/get/{flowerId}")
    fun getGarden(
        @Path("flowerId") flowerId : Int
    ) : Call<GardenRoot>
}