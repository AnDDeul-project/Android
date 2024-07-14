package com.umc.anddeul.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.FragmentHomeUploadImageBinding

class PostVPAdapter(private val imageList: List<String>): RecyclerView.Adapter<PostVPAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: FragmentHomeUploadImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            val imageView = binding.homeUploadImageIv
            val loadImage = LoadImage(imageView)
            loadImage.execute(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding : FragmentHomeUploadImageBinding = FragmentHomeUploadImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(imageList[position])
    }
}