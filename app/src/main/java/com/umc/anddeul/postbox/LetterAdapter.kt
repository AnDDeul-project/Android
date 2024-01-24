package com.umc.anddeul.postbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ItemLetterBinding

class LetterAdapter : RecyclerView.Adapter<LetterAdapter.LetterHolder>() {
    var letters: List<Letter>? = null

    override fun getItemCount(): Int {
        return letters?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
        val itemBinding = ItemLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LetterHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LetterHolder, position: Int) {
        holder.itemBinding.letterNm.text = letters?.get(position)?.name.toString()
        holder.itemBinding.root.setOnClickListener{
            itemClickListener?.onItemClickListener(it, position)
            true
        }
    }

    class LetterHolder(val itemBinding: ItemLetterBinding) : RecyclerView.ViewHolder(itemBinding.root)

    interface OnItemClickListener {
        fun onItemClickListener(view: View, pos: Int)
    }

    var itemClickListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
}