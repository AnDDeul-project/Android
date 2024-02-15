package com.umc.anddeul.checklist.network

import com.umc.anddeul.checklist.model.AddChecklist
import com.umc.anddeul.checklist.model.AddRoot
import com.umc.anddeul.checklist.model.CompleteRoot
import com.umc.anddeul.checklist.model.Root
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.io.File

interface ChecklistInterface {

    //체크리스트 추가
    @POST("/check/add")
    fun addCheckliist (
        @Body add : AddChecklist
    ) : Call<AddRoot>

    //체크리스트 불러오기
    @GET("/check/{userid}/{mode}/{date}")
    fun getChecklist (
        @Path("userid") userid : String,
        @Path("mode") mod : Boolean,
        @Path("date") date : String
    ) : Call<Root>

    //체크리스트 완료 여부 변경
    @PUT("/check/{checkid}/complete")
    fun complete (
        @Path("checkid") checkid : Int
    ) : Call<CompleteRoot>

    @PATCH("/check/img")
    fun imgPic (
        @Field("image") image : File,
        @Field("checkid") checkid: String
    )
}