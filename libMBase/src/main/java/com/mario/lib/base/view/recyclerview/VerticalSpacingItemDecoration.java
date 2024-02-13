package com.mario.lib.base.view.recyclerview;


import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int padding;

    public VerticalSpacingItemDecoration(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.bottom = padding;
        outRect.left = padding / 2;
        outRect.right = padding / 2;
    }
}
