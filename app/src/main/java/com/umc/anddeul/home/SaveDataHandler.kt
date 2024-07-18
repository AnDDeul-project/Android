package com.umc.anddeul.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.umc.anddeul.home.model.Member

class SaveDataHandler(context: Context) {

    companion object {
        private const val MEMBERS_KEY = "members"
    }

    private val spfMyId: SharedPreferences = context.getSharedPreferences("myIdSpf", Context.MODE_PRIVATE)
    private val spfMyName: SharedPreferences = context.getSharedPreferences("myNickNameSpf", Context.MODE_PRIVATE)
    private val spfFamilyLeader: SharedPreferences = context.getSharedPreferences("familyLeaderSpf", Context.MODE_PRIVATE)
    private val spfFamilyData: SharedPreferences = context.getSharedPreferences("familyDataSpf", Context.MODE_PRIVATE)

    fun saveMyId(myId: String) {
        val editor = spfMyId.edit()
        editor.putString("myId", myId)
        editor.apply()
    }

    fun getMyId(): String? {
        return spfMyId.getString("myId", null)
    }

    fun saveMyNickname(name: String) {
        val editor = spfMyName.edit()
        editor.putString("myNickName", name)
        editor.apply()
    }

    fun getMyNickname(): String? {
        return spfMyName.getString("myNickName", null)
    }

    fun saveFamilyLeader(leader: String) {
        val editor = spfFamilyLeader.edit()
        editor.putString("familyLeader", leader)
        editor.apply()
    }

    fun getFamilyLeader(): String? {
        return spfFamilyLeader.getString("familyLeader", null)
    }

    fun saveFamilyData(members: List<Member>) {
        val membersStringBuilder = StringBuilder()
        for (member in members) {
            membersStringBuilder.append("${member.snsId},${member.nickname};")
        }

        with(spfFamilyData.edit()) {
            putString(MEMBERS_KEY, membersStringBuilder.toString())
            apply()
            Log.e("familyData", membersStringBuilder.toString())
        }
    }

    private fun getMemberData(): List<Pair<String, String>> {
        val membersString = spfFamilyData.getString(MEMBERS_KEY, "") ?: return emptyList()

        // 멤버 데이터 파싱
        val membersList = mutableListOf<Pair<String, String>>()
        val membersArray = membersString.split(";")
        for (member in membersArray) {
            if (member.isNotEmpty()) {
                val memberData = member.split(",")
                if (memberData.size == 2) {
                    membersList.add(Pair(memberData[0], memberData[1]))
                }
            }
        }

        return membersList
    }

    fun getMemberNames(): List<String> {
        val members = getMemberData()
        return members.map { it.second }
    }

    fun getSnsIdByNickname(nickname: String): String? {
        val members = getMemberData()
        val member = members.find { it.second == nickname }
        return member?.first
    }
}
