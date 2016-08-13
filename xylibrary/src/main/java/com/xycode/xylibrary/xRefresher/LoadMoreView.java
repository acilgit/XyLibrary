package com.xycode.xylibrary.xRefresher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-08-12.
 */
public class LoadMoreView extends RelativeLayout {

    private static int layoutId = R.layout.layout_blank;

    public LoadMoreView(Context context) {
        super(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(layoutId, this, true);
        hide();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public static void setlayoutId(int layoutId) {
        LoadMoreView.layoutId = layoutId;

    }
}
