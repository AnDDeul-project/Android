package com.umc.anddeul.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.R
import com.umc.anddeul.databinding.ItemEmojiSelectedBinding
import com.umc.anddeul.home.model.EmojiUiModel

class EmojiRVAdpater(val context: Context, var emojiList: List<EmojiUiModel>) : RecyclerView.Adapter<EmojiRVAdpater.SelectedViewHolder>() {

    inner class SelectedViewHolder(val selectedBinding: ItemEmojiSelectedBinding) :
        RecyclerView.ViewHolder(selectedBinding.root) {
        fun bind(emoji: EmojiUiModel) {
            if (emoji.count == 0) {
                selectedBinding.itemEmojiSelectedLayout.visibility = View.GONE
            } else {
                selectedBinding.itemEmojiSelectedLayout.visibility = View.VISIBLE

                val emojiImageId = when(emoji.type) {
                    "happy" -> R.drawable.emoji_heart
                    "laugh" -> R.drawable.emoji_fun
                    else -> R.drawable.emoji_sad
                }
                selectedBinding.homeEmojiHappyOne.setImageDrawable(
                    ContextCompat.getDrawable(
                        context, emojiImageId
                    )
                )

                selectedBinding.homeEmojiCount.text = emoji.count.toString()

                if(emoji.selected) {
                    selectedBinding.itemEmojiSelectedLayout.setBackgroundResource(R.drawable.emoji_back_selected)
                    selectedBinding.homeEmojiCount.setTextColor(ContextCompat.getColor(context, R.color.tertiary))
                } else {
                    selectedBinding.itemEmojiSelectedLayout.setBackgroundResource(R.drawable.emoji_back_non_select)
                    selectedBinding.homeEmojiCount.setTextColor(ContextCompat.getColor(context, R.color.primary))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedViewHolder {
        val selectedBinding = ItemEmojiSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedViewHolder(selectedBinding)
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }

    override fun onBindViewHolder(holder: SelectedViewHolder, position: Int) {
        val emoji = emojiList[position]
        holder.bind(emoji)
    }
}