package com.umc.anddeul.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.FragmentMypagePostImageBinding

class MyPageProfileRVAdapter(private val myPostList: List<String>) : RecyclerView.Adapter<MyPageProfileRVAdapter.MyPageViewHolder>(){

    inner class MyPageViewHolder (val binding: FragmentMypagePostImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            binding.mypagePostIv.setImageResource(R.drawable.img_upload_feed)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageViewHolder {
        val binding : FragmentMypagePostImageBinding = FragmentMypagePostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyPageViewHolder(binding)
    }

    override fun getItemCount(): Int = myPostList.size

    override fun onBindViewHolder(holder: MyPageViewHolder, position: Int) {
        holder.bind(myPostList[position])
    }
}