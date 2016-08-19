package com.xycode.xylibrary.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xycode.xylibrary.R;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseFlowTagLayout extends ViewGroup {

    private static final String TAG = BaseFlowTagLayout.class.getSimpleName();

    /**
     * FlowLayout not support checked
     */
    public static final int FLOW_TAG_CHECKED_NONE = 0;
    /**
     * FlowLayout support single-select
     */
    public static final int FLOW_TAG_CHECKED_SINGLE = 1;
    /**
     * FlowLayout support multi-select
     */
    public static final int FLOW_TAG_CHECKED_MULTI = 2;
    private SparseArray<View> viewList;

    protected int tagType;

    /**
     * Should be used by subclasses to listen to changes in the dataset
     */
//    AdapterDataSetObserver mDataSetObserver;

    /**
     * The adapter containing the data to be displayed by this view
     */
//    ListAdapter mAdapter;

    /**
     * the tag click event callback
     */
    OnTagClickListener onTagClickListener;

    /**
     * the tag select event callback
     */
    OnTagSelectListener onTagSelectListener;

    /**
     * default tag check mode
     */
    private int tagCheckMode = FLOW_TAG_CHECKED_NONE;

    private List dataList;


    /**
     * save selected tag
     */
    private SparseBooleanArray checkedTagArray = new SparseBooleanArray();

    public BaseFlowTagLayout(Context context) {
        super(context, null);
    }

    public BaseFlowTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewList = new SparseArray<>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseFlowTagLayout);

        tagType = a.getInt(R.styleable.BaseFlowTagLayout_tagType, 0);

        a.recycle();

        dataList = new ArrayList<>();
    }

    public BaseFlowTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int resultWidth = 0;
        int resultHeight = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        if (getChildCount()==0) {
            setMeasuredDimension(sizeWidth, 0);
            return;
        }
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            int realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin;
            int realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin;

            if ((lineWidth + realChildWidth) > sizeWidth) {
                resultWidth = Math.max(lineWidth, realChildWidth);
                resultHeight += realChildHeight;
                lineWidth = realChildWidth;
                lineHeight = realChildHeight;
            } else {
                lineWidth += realChildWidth;
                lineHeight = Math.max(lineHeight, realChildHeight);
            }

            if (i == childCount - 1) {
                resultWidth = Math.max(lineWidth, resultWidth);
                resultHeight += lineHeight;
            }

            setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : resultWidth,
                    modeHeight == MeasureSpec.EXACTLY ? sizeHeight : resultHeight);

        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int flowWidth = getWidth();

        int childLeft = 0;
        int childTop = 0;
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

            if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) {
                childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                childLeft = 0;
            }
            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = childLeft + mlp.leftMargin + childWidth;
            int bottom = childTop + mlp.topMargin + childHeight;
            childView.layout(left, top, right, bottom);

            childLeft += (mlp.leftMargin + childWidth + mlp.rightMargin);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

/*    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadData();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }*/

    protected abstract View bindChildView(View contentView, List list, int position);

    protected abstract int getLayoutId();

    public void setDataList(List dataList) {
        if (dataList != null && (this.dataList == null || !dataList.containsAll(this.dataList) || !this.dataList.containsAll(dataList))) {
            this.dataList = dataList;
            viewList.clear();
            removeAllViews();
            reloadData();
        }
    }

    /**
     */
    private void reloadData() {
        viewList.clear();
        removeAllViews();

        for (int i = 0; i < dataList.size(); i++) {
            final int j = i;
            checkedTagArray.put(i, false);
            View contentView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this, false);
            final View childView = bindChildView(contentView, dataList, i);
            childView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
            addView(childView, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            viewList.put(i, childView);
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (tagCheckMode) {
                        case FLOW_TAG_CHECKED_NONE:
                            if (onTagClickListener != null) {
                                onTagClickListener.onItemClick(childView, dataList, j);
                            }
                            break;
                        case FLOW_TAG_CHECKED_SINGLE:
                            if (checkedTagArray.get(j)) {
                                checkedTagArray.put(j, false);
                                childView.setSelected(false);
                                if (onTagSelectListener != null) {
                                    for (int i = 0; i < checkedTagArray.size(); i++) {
                                        onTagSelectListener.onItemSelect(viewList.get(i), dataList, i, checkedTagArray.get(i));
                                    }
                                }
                                return;
                            }

                            for (int k = 0; k < dataList.size(); k++) {
                                checkedTagArray.put(k, false);
                                getChildAt(k).setSelected(false);
                            }
                            checkedTagArray.put(j, true);
                            childView.setSelected(true);

                            if (onTagSelectListener != null) {
                                for (int i = 0; i < checkedTagArray.size(); i++) {
                                    onTagSelectListener.onItemSelect(viewList.get(i), dataList, i, checkedTagArray.get(i));
                                }
                            }
                            break;
                        case FLOW_TAG_CHECKED_MULTI:
                            if (checkedTagArray.get(j)) {
                                checkedTagArray.put(j, false);
                                childView.setSelected(false);
                            } else {
                                checkedTagArray.put(j, true);
                                childView.setSelected(true);
                            }
                            if (onTagSelectListener != null) {
                                List<Integer> list = new ArrayList<>();
                                for (int k = 0; k < dataList.size(); k++) {
                                    if (checkedTagArray.get(k)) {
                                        list.add(k);
                                    }
                                    onTagSelectListener.onItemSelect(viewList.get(k), dataList, k, checkedTagArray.get(k));
                                }
                            }
                            break;
                    }
                }
            });
        }
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.onTagSelectListener = onTagSelectListener;
    }

    /**
     *
     * @param adapter
     */
  /*  public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

            viewList.clear();
        removeAllViews();
        mAdapter = adapter;

        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }*/

    /**
     *
     * @return
     */
    public int getTagCheckMode() {
        return tagCheckMode;
    }

    /**
     *
     * @param tagMode
     */
    public void setTagCheckedMode(int tagMode) {
        this.tagCheckMode = tagMode;
    }


    public interface OnTagClickListener {
        void onItemClick(View view, List dataList, int position);
    }

    public interface OnTagSelectListener {
        void onItemSelect(View childView, List dataList, int pos, boolean selected);
    }

        public static <T extends View> T getView(View parent, int viewId) {
            return (T) parent.findViewById(viewId);
        }

        public static View setText(View parent,int viewId, String text) {
            View view = getView(parent, viewId);
            if (view != null) {
                if (view instanceof EditText) {
                    ((EditText) view).setText(text);
                } else if (view instanceof Button) {
                    ((Button) view).setText(text);
                } else if (view instanceof TextView) {
                    ((TextView) view).setText(text);
                }
            }
            return parent;
        }

        public static View setTextColor(View parent,int viewId, @ColorInt int textColor) {
            View view = getView(parent,viewId);
            if (view != null) {
                if (view instanceof EditText) {
                    ((EditText) view).setTextColor(textColor);
                } else if (view instanceof Button) {
                    ((Button) view).setTextColor(textColor);
                } else if (view instanceof TextView) {
                    ((TextView) view).setTextColor(textColor);
                }
            }
            return parent;
        }


}
