package com.xycode.xylibrary.uiKit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.xycode.xylibrary.R;


/**
 * Created by XY on 2016-07-28.
 */
public class XScrollView extends ScrollView {

    private Context context;
    private int attr_maxHeight;

    public XScrollView(Context context) {
        super(context, null);
    }

    public XScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XScrollView);
        attr_maxHeight = a.getDimensionPixelSize(R.styleable.XScrollView_maxHeight, 0);
        a.recycle();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (attr_maxHeight > 0) {
            //此处是关键，设置控件高度（在此替换成自己需要的高度）
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(attr_maxHeight, MeasureSpec.AT_MOST);
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
