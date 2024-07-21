package com.umc.anddeul.checklist.service

import android.util.Log
import com.umc.anddeul.checklist.model.ChecklistAlarm
import com.umc.anddeul.checklist.network.ChecklistInterface
import com.umc.anddeul.common.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChecklistAlarmService {
    val retrofit = RetrofitManager.getRetrofitInstance()
    val service = retrofit.create(ChecklistInterface::class.java)

    fun alarmApi (callback : (ChecklistAlarm?) -> Unit) {
        val alarmCall : Call<ChecklistAlarm> = service.alarm("checklist")
        alarmCall.enqueue(object : Callback<ChecklistAlarm> {
            override fun onResponse(call: Call<ChecklistAlarm>, response: Response<ChecklistAlarm>) {
                Log.d("Checklist AlarmService Fail", "AlarmCall : ${response.code()}")
                Log.d("Checklist AlarmService Fail", "AlarmCall : ${response.body()}")
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            callback(response.body())
                        }
                        401 -> {
                            callback(response.body())
                        }
                        500 -> {
                            callback(response.body())
                        }
                        4001 -> {
                            callback(response.body())
                        }
                        else -> {
                            callback(null)}
                        }
                }
            }
            override fun onFailure(call: Call<ChecklistAlarm>, t: Throwable) {
                Log.d("Checklist AlarmService Fail", "AlarmCall : ${t.message}")
                callback(null)
            }
        })
    }
}