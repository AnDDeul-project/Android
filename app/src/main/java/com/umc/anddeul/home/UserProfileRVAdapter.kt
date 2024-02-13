package com.umc.anddeul.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.FragmentUserProfilePostImageBinding

class UserProfileRVAdapter(private val userPostList : List<String>?, private val postIdList: List<Int>?) : RecyclerView.Adapter<UserProfileRVAdapter.UserProfileViewHolder>() {

    interface MyItemClickInterface {
        fun onItemClick(postIdx: Int)
    }

    private lateinit var mItemClickInterface: MyItemClickInterface

    fun setMyItemClickListener(itemClickListener : MyItemClickInterface) {
        mItemClickInterface = itemClickListener
    }

    inner class UserProfileViewHolder(val binding: FragmentUserProfilePostImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Log.e("UserProfileBind", "bind image : ${image}")
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
        val currentImage = userPostList?.getOrNull(position)

        if (currentImage != null) {
            holder.bind(currentImage)
        } else {
            // userPostList가 null이면 userProfilePostIv 안보이게 설정
            holder.binding.userProfilePostIv.visibility = View.GONE
        }

        val postIdx = postIdList?.getOrNull(position)
        holder.itemView.setOnClickListener {
            if (postIdx != null) {
                mItemClickInterface.onItemClick(postIdx)
            }
        }
    }

}