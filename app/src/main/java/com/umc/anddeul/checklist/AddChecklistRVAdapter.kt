package com.umc.anddeul.checklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.checklist.model.Checklist
import com.umc.anddeul.databinding.ItemAddChecklistBinding

class AddChecklistRVAdapter() : RecyclerView.Adapter<AddChecklistRVAdapter.ViewHolder>() {
    var checklist: List<com.umc.anddeul.checklist.model.Checklist>? = null

    override fun getItemCount(): Int {
        return checklist?.size ?: 0
    }

    fun setChecklistData(checklist: List<com.umc.anddeul.checklist.model.Checklist>) {
        this.checklist = checklist
    }

    override fun onBindViewHolder(holder: AddChecklistRVAdapter.ViewHolder, position: Int) {
        holder.bind(checklist!!.get(position))
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AddChecklistRVAdapter.ViewHolder {
        val binding : ItemAddChecklistBinding = ItemAddChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemAddChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist: Checklist) {
            binding.checkliAddTvChecklist.text = checklist.content
            binding.checkliAddTvWriter.text = checklist.sender + "님이 남기셨습니다."
            if (binding.checkliAddIvPhoto != null) {
                binding.checkliAddIvPhoto.visibility = View.VISIBLE
            }
            if (checklist.complete == 0) { //체크 안 되어 있을 때
                binding.checkliAddBtnChecked.visibility = View.INVISIBLE
                binding.checkliAddBtnChecking.visibility = View.VISIBLE
            } else {
                binding.checkliAddBtnChecked.visibility = View.VISIBLE
                binding.checkliAddBtnChecking.visibility = View.INVISIBLE
                binding.checkliAddTvWriter.setTextColor(Color.parseColor("#BFBFBF"))
                binding.checkliAddTvChecklist.setTextColor(Color.parseColor("#BFBFBF"))
            }
        }
    }
}