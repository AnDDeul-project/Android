package com.umc.anddeul.home

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class DragAndDropHelper(private val viewPager: ViewPager2, private val listener: OnItemMovedListener) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {

    interface OnItemMovedListener {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
    }

    // 아이템을 이동할 때
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        listener.onItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            // 드래그 시작될 때 모든 아이템 크기 조정
            scaleAllItems(0.9f)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        // 드래그가 종료될 때 모든 아이템 크기 원래대로 복원
        scaleAllItems(1f)
    }

    private fun scaleAllItems(scale: Float) {
        for (i in 0 until viewPager.childCount) {
            val page = viewPager.getChildAt(i)
            if (page is RecyclerView) {
                for (j in 0 until page.childCount) {
                    val holder = page.getChildViewHolder(page.getChildAt(j))
                    holder.itemView.scaleX = scale
                    holder.itemView.scaleY = scale
                }
            }
        }
    }
}
