package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-07-28.
 */
public class NoScrollViewPager extends ViewPager {

    public static final int FIT_SINGLE_PAGE = 0;
    public static final int FIT_SHOWN_PAGE = 1;
    public static final int FIT_MAXHEIGHT_PAGE = 2;

    private boolean isScrollable = false;
    private boolean measureAllPages = false;
    private int measurePageType;

    public NoScrollViewPager(Context context) {
        super(context, null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NoScrollViewPager);
        isScrollable = a.getBoolean(R.styleable.NoScrollViewPager_enableScroll, false);
        measureAllPages = a.getBoolean(R.styleable.NoScrollViewPager_measureAllPages, false);
        measurePageType = a.getInt(R.styleable.NoScrollViewPager_measurePageType, FIT_SINGLE_PAGE);
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

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!measureAllPages) return;

        /*int maxHeight = 0;
        if (getAdapter().instantiateItem(this, getCurrentItem()) instanceof View) {
            if (getAdapter() != null && getAdapter().getCount() > 0) {
                for (int i = 0; i < getAdapter().getCount(); i++) {
                    int fragmentHeight = measurePage(((View) getAdapter().instantiateItem(this, i)));
                    if (fragmentHeight > maxHeight) {
                        maxHeight = fragmentHeight;
                    }
                }
            }
        } else {
            maxHeight = measurePage(((Fragment) getAdapter().instantiateItem(this, getCurrentItem())).getView());
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);*/

        int height = 0;

        switch (measurePageType) {
            case FIT_SINGLE_PAGE :
            case FIT_MAXHEIGHT_PAGE :
                //for to all child height
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    int h = child.getMeasuredHeight();
                    //use maximum view Height
                    if (h > height)
                        height = h;
                }
                break;
            case FIT_SHOWN_PAGE :
                View child = getChildAt(getCurrentItem());
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                height = child.getMeasuredHeight();
                break;
            default:
                break;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    public int measurePage(View view) {
        if (view == null)
            return 0;
        view.measure(0, 0);
        return view.getMeasuredHeight();
    }


}
