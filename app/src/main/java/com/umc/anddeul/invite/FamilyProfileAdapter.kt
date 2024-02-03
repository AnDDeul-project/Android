package com.umc.anddeul.invite

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umc.anddeul.databinding.ItemFamilyIconBinding

class FamilyProfileAdapter : RecyclerView.Adapter<FamilyProfileAdapter.FamilyProfileHolder>() {
    var families: List<Family>? = null

    override fun getItemCount(): Int {
        return families?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyProfileHolder {
        val itemBinding = ItemFamilyIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FamilyProfileHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FamilyProfileHolder, position: Int) {
        val currentFamily = families?.get(position)

        val imageUrl = currentFamily?.profile

        imageUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.itemBinding.imageView10)
        }
    }

    class FamilyProfileHolder(val itemBinding: ItemFamilyIconBinding) : RecyclerView.ViewHolder(itemBinding.root)

    class HorizontalSpaceDecoration(private val leftSpace: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)

            if (position > 0) {
                outRect.left = -leftSpace
            } else {
                outRect.left = 0  // No negative space for the first item
            }
        }
    }

    class DimensionUtils {
        companion object {
            fun dpToPx(context: Context, dp: Float): Int {
                val scale = context.resources.displayMetrics.density
                return (dp * scale + 0.5f).toInt()
            }
        }
    }
}
