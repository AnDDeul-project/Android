package com.umc.anddeul.postbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ItemLetterlistBinding
import com.umc.anddeul.postbox.model.Post

class LetterListAdapter : RecyclerView.Adapter<LetterListAdapter.LetterListHolder>() {
    var letters: List<Post>? = null

    override fun getItemCount(): Int {
        return letters?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterListHolder {
        val itemBinding = ItemLetterlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LetterListHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LetterListHolder, position: Int) {
        if (letters?.get(position)?.voice == 0.toLong()){
            holder.itemBinding.letterIv.visibility = View.VISIBLE
            holder.itemBinding.recordIv.visibility = View.GONE
        }
        if (letters?.get(position)?.voice == 1.toLong()){
            holder.itemBinding.letterIv.visibility = View.GONE
            holder.itemBinding.recordIv.visibility = View.VISIBLE
        }
        holder.itemBinding.userNmTv.text = letters?.get(position)?.senderIdx
        if(letters?.get(position)?.voice == 0.toLong()){
//        holder.itemBinding.detailTv.text = letters?.get(position)?.content.toString()
        }
        else {
            holder.itemBinding.detailTv.text = "음성 메세지가 도착했습니다."
        }
        holder.itemBinding.root.setOnClickListener{
            itemClickListener?.onItemClickListener(it, position)
            true
        }
    }

    class LetterListHolder(val itemBinding: ItemLetterlistBinding) : RecyclerView.ViewHolder(itemBinding.root)

    interface OnItemClickListener {
        fun onItemClickListener(view: View, pos: Int)
    }

    var itemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
}