package com.umc.anddeul.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ActivityPostWriteSelectedBinding

class SelectedVPAdapter(private val selectedImages: MutableList<Uri>) :
    RecyclerView.Adapter<SelectedVPAdapter.SelectedViewHolder>() {

    inner class SelectedViewHolder(val binding: ActivityPostWriteSelectedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(selected : Uri) {
            binding.uploadWriteSelectedIv.setImageURI(selected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedViewHolder {
        val binding : ActivityPostWriteSelectedBinding = ActivityPostWriteSelectedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return SelectedViewHolder(binding)
    }

    override fun getItemCount(): Int = selectedImages.size

    override fun onBindViewHolder(holder: SelectedViewHolder, position: Int) {
        holder.bind(selectedImages[position])
    }

    fun addImage(selected: Uri) {
        // 이미 리스트에 있는지 확인
        if (!selectedImages.contains(selected)) {
            selectedImages.add(selected)

            // 새로운 프래그먼트가 추가됐을 때 viewPager에게 새로운 프래그먼트가 추가됐음을 알려줌
            notifyItemInserted(selectedImages.size - 1)
        }
    }
}