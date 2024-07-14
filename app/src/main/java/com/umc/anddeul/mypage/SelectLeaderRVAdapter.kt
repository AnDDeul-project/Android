package com.umc.anddeul.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ItemSelectLeaderBinding

class SelectLeaderRVAdapter(
    private val nameList: List<String>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SelectLeaderRVAdapter.LeaderViewHolder>() {

    private var selectedPosition = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        val binding = ItemSelectLeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return LeaderViewHolder(binding)
    }

    override fun getItemCount(): Int = nameList.size

    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        holder.bind(nameList[position], position == selectedPosition)
    }

    inner class LeaderViewHolder(private val binding: ItemSelectLeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    if (currentPosition != selectedPosition) {
                        val previousSelectedPosition = selectedPosition
                        selectedPosition = currentPosition
                        itemClickListener.onItemClick(currentPosition)
                        notifyItemChanged(previousSelectedPosition)
                        notifyItemChanged(selectedPosition)
                    }
                }
            }
        }

        fun bind(name: String, isSelected: Boolean) {
            binding.itemSelectLeaderNameTv.text = name

            if (isSelected) {
                binding.itemSelectLeaderBtn.setImageResource(R.drawable.checkli_img_checked_circle)
            } else {
                binding.itemSelectLeaderBtn.setImageResource(R.drawable.checkli_img_check_circle)
            }
        }
    }

    fun getSelectedMember(): String? {
        return if (selectedPosition != -1) nameList[selectedPosition] else null
    }
}
