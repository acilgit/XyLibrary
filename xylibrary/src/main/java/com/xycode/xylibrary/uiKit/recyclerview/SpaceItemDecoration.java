package com.xycode.xylibrary.uiKit.recyclerview;


import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mOrientation;
    private final int mSize;

    public SpaceItemDecoration(int orientation, int size) {
        mOrientation = orientation;
        mSize = size;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        switch (mOrientation) {
            case LinearLayoutManager.HORIZONTAL:
                outRect.right = mSize;

                break;

            case LinearLayoutManager.VERTICAL:
                outRect.bottom = mSize;

                break;

            default:

                break;
        }
    }
}