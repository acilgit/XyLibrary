package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-07-28.
 */
public class NoScrollViewPager extends ViewPager {
    private boolean isScrollable = false;

    public NoScrollViewPager(Context context) {
        super(context, null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NoScrollViewPager);
        isScrollable = a.getBoolean(R.styleable.NoScrollViewPager_enableScroll, false);
        a.recycle();
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScrollable) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isScrollable) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }
    }
}
