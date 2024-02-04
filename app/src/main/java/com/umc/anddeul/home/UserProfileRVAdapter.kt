package com.umc.anddeul.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentUserProfilePostImageBinding

class UserProfileRVAdapter(private val userPostList : List<String>) : RecyclerView.Adapter<UserProfileRVAdapter.UserProfileViewHolder>() {
    inner class UserProfileViewHolder(val binding: FragmentUserProfilePostImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            binding.userProfilePostIv.setImageResource(R.drawable.img_upload_feed)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileViewHolder {
        val binding : FragmentUserProfilePostImageBinding = FragmentUserProfilePostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UserProfileViewHolder(binding)
    }

    override fun getItemCount(): Int = userPostList.size

    override fun onBindViewHolder(holder: UserProfileViewHolder, position: Int) {
        holder.bind(userPostList[position])
    }

}