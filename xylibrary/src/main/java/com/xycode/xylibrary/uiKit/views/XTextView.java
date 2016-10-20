package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-10-18.
 */

public class XTextView extends TextView {
    private int leftHeight = -1;
    private int leftWidth = -1;
    private int rightHeight = -1;
    private int rightWidth = -1;
    private int topHeight = -1;
    private int topWidth = -1;
    private int bottomHeight = -1;
    private int bottomWidth = -1;

    public XTextView(Context context) {
        super(context);
    }

    public XTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public XTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTextView, defStyle, 0);
        if (a != null) {
            int count = a.getIndexCount();
            int index;
            for (int i = 0; i < count; i++) {
                index = a.getIndex(i);
                if (index == R.styleable.XTextView_bottomHeight) {
                    bottomHeight = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_bottomWidth) {
                    bottomWidth = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_leftHeight) {
                    leftHeight = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_leftWidth) {
                    leftWidth = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_rightHeight) {
                    rightHeight = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_rightWidth) {
                    rightWidth = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_topHeight) {
                    topHeight = a.getDimensionPixelSize(index, -1);
                } else if (index == R.styleable.XTextView_topWidth) {
                    topWidth = a.getDimensionPixelSize(index, -1);
                }
            }
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Drawable[] drawables = getCompoundDrawables();
        int dir = 0;
        // 0-left; 1-top; 2-right; 3-bottom;
        for (Drawable drawable : drawables) {
            setImageSize(drawable, dir++);
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    private void setImageSize(Drawable d, int dir) {
        if (d == null) {
            return;
        }
        int height = -1;
        int width = -1;
        switch (dir) {
            case 0:
                // left  
                height = leftHeight;
                width = leftWidth;
                break;
            case 1:
                // top  
                height = topHeight;
                width = topWidth;
                break;
            case 2:
                // right  
                height = rightHeight;
                width = rightWidth;
                break;
            case 3:
                // bottom  
                height = bottomHeight;
                width = bottomWidth;
                break;
        }
        if (width != -1 && height != -1) {
            d.setBounds(0, 0, width, height);
        }
    }
}
