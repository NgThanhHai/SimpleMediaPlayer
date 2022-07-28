package com.example.simplemediaplayer.ext

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(var horizontalSpacing: Int,var verticalSpacing: Int, var alignLeft: Boolean) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = horizontalSpacing / 2
        outRect.right = horizontalSpacing / 2
        outRect.top = verticalSpacing /2
        outRect.bottom = verticalSpacing

        if (alignLeft) {
            outRect.left = 0
        }
    }
}