package com.umc.anddeul.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TokenManager {
    private const val SHARED_PREF_NAME = "myToken"
    private const val KEY_JWT_TOKEN = "jwtToken"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_JWT_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_JWT_TOKEN)
        editor.apply()
    }
}
