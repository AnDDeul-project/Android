package com.umc.anddeul.home

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragAndDropHelper(
    private val listener: OnItemMovedListener,
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {

    // 아이템을 이동할 때
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        listener.onItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            listener.onDraggingStarted()
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        listener.onDraggingFinished()
    }

    interface OnItemMovedListener {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onDraggingStarted()
        fun onDraggingFinished()
    }
}
