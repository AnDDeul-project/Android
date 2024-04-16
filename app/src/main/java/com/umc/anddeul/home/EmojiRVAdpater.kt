package com.umc.anddeul.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umc.anddeul.databinding.ItemEmojiNonSelectedBinding
import com.umc.anddeul.databinding.ItemEmojiSelectedBinding
import com.umc.anddeul.home.model.Emoji

class EmojiRVAdpater(var emojiList: List<Emoji>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SELECTED = 0
        const val NON_SELECTED = 1
    }

    inner class SelectedViewHolder(val selectedBinding: ItemEmojiSelectedBinding) : RecyclerView.ViewHolder(selectedBinding.root) {
        fun bind(emojiKind: String, emojiCount: Int) {
            Log.e("emojiViewHolder", "$emojiKind, $emojiCount")
            if (emojiCount == 0) {
                selectedBinding.itemEmojiSelectedLayout.visibility = View.GONE
            } else {
                selectedBinding.itemEmojiSelectedLayout.visibility = View.VISIBLE
                if(emojiKind == "happy") {
                    selectedBinding.homeEmojiHappyOne.visibility = View.VISIBLE
                    selectedBinding.homeEmojiFunOne.visibility = View.GONE
                    selectedBinding.homeEmojiSadOne.visibility = View.GONE
                    selectedBinding.homeEmojiCount.text = emojiCount.toString()
                }
                if(emojiKind == "laugh") {
                    selectedBinding.homeEmojiHappyOne.visibility = View.GONE
                    selectedBinding.homeEmojiFunOne.visibility = View.VISIBLE
                    selectedBinding.homeEmojiSadOne.visibility = View.GONE
                    selectedBinding.homeEmojiCount.text = emojiCount.toString()

                }
                if(emojiKind == "sad") {
                    selectedBinding.homeEmojiHappyOne.visibility = View.GONE
                    selectedBinding.homeEmojiFunOne.visibility = View.GONE
                    selectedBinding.homeEmojiSadOne.visibility = View.VISIBLE
                    selectedBinding.homeEmojiCount.text = emojiCount.toString()

                }
            }
        }
    }

    inner class NonSelectedViewHolder(val nonSelectedBinding: ItemEmojiNonSelectedBinding) : RecyclerView.ViewHolder(nonSelectedBinding.root) {
        fun bind(emojiKind: String, emojiCount: Int) {
            if (emojiCount == 0) {
                nonSelectedBinding.itemEmojiNonSelectedLayout.visibility = View.GONE
            } else {
                nonSelectedBinding.itemEmojiNonSelectedLayout.visibility = View.VISIBLE
                if(emojiKind == "happy") {
                    nonSelectedBinding.homeEmojiNonHappyOne.visibility = View.VISIBLE
                    nonSelectedBinding.homeEmojiNonFunOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonSadOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonCount.text = emojiCount.toString()
                }
                if(emojiKind == "laugh") {
                    nonSelectedBinding.homeEmojiNonHappyOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonFunOne.visibility = View.VISIBLE
                    nonSelectedBinding.homeEmojiNonSadOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonCount.text = emojiCount.toString()
                }
                if(emojiKind == "sad") {
                    nonSelectedBinding.homeEmojiNonHappyOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonFunOne.visibility = View.GONE
                    nonSelectedBinding.homeEmojiNonSadOne.visibility = View.VISIBLE
                    nonSelectedBinding.homeEmojiNonCount.text = emojiCount.toString()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SELECTED -> {
                val selectedBinding = ItemEmojiSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SelectedViewHolder(selectedBinding)
            }
            NON_SELECTED -> {
                val nonSelectedBinding = ItemEmojiNonSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NonSelectedViewHolder(nonSelectedBinding)
            }
            else -> throw IllegalArgumentException("이모지 view type 에러")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (emojiList[position].selected) {
            true -> SELECTED
            false -> NON_SELECTED
        }
    }
    override fun getItemCount(): Int {
        return emojiList.count{ it.count != 0 }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val emojiCount = emojiList[position].count

        val emojiType = when (position) {
            0 -> "happy"
            1 -> "laugh"
            else -> "sad"
        }

        when (holder) {
            is SelectedViewHolder -> {
                holder.bind(emojiType, emojiCount)
            }
            is NonSelectedViewHolder -> {
                holder.bind(emojiType, emojiCount)
            }
        }
    }
}