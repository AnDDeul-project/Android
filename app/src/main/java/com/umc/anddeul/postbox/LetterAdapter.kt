package com.umc.anddeul.postbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ItemLetterBinding
import com.umc.anddeul.postbox.model.Post

class LetterAdapter : RecyclerView.Adapter<LetterAdapter.LetterHolder>() {
    var letters: List<Post>? = null
        set(value) {
            field = value?.sortedWith(compareBy { it.isRead })
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return letters?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
        val itemBinding = ItemLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LetterHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LetterHolder, position: Int) {
        holder.itemBinding.letterNm.text = letters?.get(position)?.senderIdx
        if (letters?.get(position)?.isRead == 1.toLong()){
            holder.itemBinding.miniLetterIv.setImageResource(R.drawable.ic_post2)
        } else {
            holder.itemBinding.miniLetterIv.setImageResource(R.drawable.ic_post)
        }
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