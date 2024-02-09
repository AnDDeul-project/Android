package com.umc.anddeul.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentUserProfilePostImageBinding

class UserProfileRVAdapter(private val userPostList : List<String>?) : RecyclerView.Adapter<UserProfileRVAdapter.UserProfileViewHolder>() {
    inner class UserProfileViewHolder(val binding: FragmentUserProfilePostImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            binding.userProfilePostIv.setImageResource(R.drawable.img_upload_feed)
            val imageView = binding.userProfilePostIv
            val loadImage = LoadImage(imageView)
            loadImage.execute(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileViewHolder {
        val binding : FragmentUserProfilePostImageBinding = FragmentUserProfilePostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UserProfileViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userPostList?.size ?: 0
    }
    override fun onBindViewHolder(holder: UserProfileViewHolder, position: Int) {
        userPostList?.get(position)?.let { holder.bind(it) }
    }

}