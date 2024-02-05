package com.umc.anddeul.checklist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ItemAddChecklistBinding
import com.umc.anddeul.databinding.ItemChecklistBinding

class AddChecklistRVAdapter(private val checklist: ArrayList<Checklist>) : RecyclerView.Adapter<AddChecklistRVAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return checklist.size
    }

    override fun onBindViewHolder(holder: AddChecklistRVAdapter.ViewHolder, position: Int) {
        holder.bind(checklist[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AddChecklistRVAdapter.ViewHolder {
        val binding : ItemAddChecklistBinding = ItemAddChecklistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemAddChecklistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist: Checklist) {
            binding.checkliAddTvChecklist.text = checklist.contents
            binding.checkliAddTvWriter.text = checklist.writer + "님이 남기셨습니다."
            if (binding.checkliAddIvPhoto != null) {
                binding.checkliAddIvPhoto.visibility = View.VISIBLE
            }
            if (checklist.checked == false) { //체크 안 되어 있을 때
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