/**
 * Copyright 2017 ChenHao Dendi
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xycode.xylibrary.uiKit.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


import com.xycode.xylibrary.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FloatingBarItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int mTitleHeight;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mTextHeight;
    private int mTextBaselineOffset;
    private int mTextStartMargin;
    private int adapterHeaderCount = 0;

    private ITitleTextGetter iTitleTextGetter;
    /**
     * Integer means the related position of the RecyclerView#getViewAdapterPosition()
     * (the position of the view in original adapter's list)
     * String means the title to be drawn
     */
    private Map<Integer, String> headerList;

    public FloatingBarItemDecoration(Context context, List sourceList, Options options, ITitleTextGetter iTitleTextGetter) {
        this.mContext = context;
        Resources resources = mContext.getResources();
        this.iTitleTextGetter = iTitleTextGetter;
        this.headerList = preHeaderList(sourceList);
        this.mTitleHeight = resources.getDimensionPixelSize(options.titleHeight);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(mContext, options.backgroundColorRes));

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(mContext, options.textColor));
        mTextPaint.setTextSize(mContext.getResources().getDimensionPixelSize(options.textSize));

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int) (fm.bottom - fm.top);
        mTextBaselineOffset = (int) fm.bottom;
        mTextStartMargin = resources.getDimensionPixelOffset(options.textStartMargin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        outRect.set(0, headerList.containsKey(position) ? mTitleHeight : 0, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewAdapterPosition();
            if (!headerList.containsKey(position)) {
                continue;
            }
            drawTitleArea(c, left, right, child, params, position);
        }
    }

    private void drawTitleArea(Canvas c, int left, int right, View child,
                               RecyclerView.LayoutParams params, int position) {
        final int rectBottom = child.getTop() - params.topMargin;
        c.drawRect(left, rectBottom - mTitleHeight, right,
                rectBottom, mBackgroundPaint);
        c.drawText(headerList.get(position), child.getPaddingLeft() + mTextStartMargin,
                rectBottom - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int[] positions;
        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            positions = ((StaggeredGridLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPositions(null);
        } else {
            positions = new int[1];
            positions[0] = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        }
        if (positions[0] == RecyclerView.NO_POSITION) {
            return;
        }
/*        if (positions[0] < adapterHeaderCount) {
            return;
        }*/
        View child = parent.findViewHolderForAdapterPosition(positions[0]).itemView;
        String initial = getTag(positions[0]);
        if (initial == null) {
            return;
        }

        boolean flag = false;
        if (getTag(positions[0] + 1) != null && !initial.equals(getTag(positions[0] + 1))) {
            if (child.getHeight() + child.getTop() < mTitleHeight) {
                c.save();
                flag = true;
                c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
            }
        }

        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mTitleHeight,
                mBackgroundPaint);
        c.drawText(initial,
                child.getPaddingLeft() + mTextStartMargin,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset,
                mTextPaint);
        if (flag) {
            c.restore();
        }
    }

    private String getTag(int position) {
        while (position >= 0) {
            if (headerList.containsKey(position)) {
                return headerList.get(position);
            }
            position--;
        }
        return null;
    }

    /**
     * 设置列表
     *
     * @param list               数据源
     * @param adapterHeaderCount 适配器中的自定义的Header的数量
     */
    public void setList(List list, int adapterHeaderCount) {
        this.adapterHeaderCount = adapterHeaderCount;
        headerList = preHeaderList(list);
    }

    public void setList(List list) {
        headerList = preHeaderList(list);
    }

    public static class Options {
        private int titleHeight;
        private int backgroundColorRes;
        private int textColor;
        private int textSize;
        private int textStartMargin = R.dimen.sideMargin;

        public Options(@DimenRes int titleHeight) {
            this.titleHeight = titleHeight;
        }

        /**
         * 设置标题栏的背景颜色
         *
         * @param backgroundColorRes
         * @return
         */
        public Options setBackgroundColor(@ColorRes int backgroundColorRes) {
            this.backgroundColorRes = backgroundColorRes;
            return this;
        }

        /**
         * 设置背景栏字体颜色、大小
         *
         * @param textColor
         * @param textSize
         * @return
         */
        public Options setTextPaint(@ColorRes int textColor, @DimenRes int textSize) {
            this.textColor = textColor;
            this.textSize = textSize;
            return this;
        }

        /**
         * 设置标题左偏移量
         *
         * @param textStartMargin
         * @return
         */
        public Options setTextStartMargin(@DimenRes int textStartMargin) {
            this.textStartMargin = textStartMargin;
            return this;
        }
    }

    /**
     * calculate the TAG and add to {@Link #mHeaderlist}
     */
    private Map<Integer, String> preHeaderList(List list) {
        if (headerList == null) {
            headerList = new LinkedHashMap<>();
        } else {
            headerList.clear();
        }
        if (list == null || list.size() == 0 || iTitleTextGetter == null) {
            return headerList;
        }
        addHeaderToList(0, iTitleTextGetter.getTitle(list.get(0)));
        for (int i = 2; i < list.size(); i++) {
            if (!iTitleTextGetter.getTitle(list.get(i - 1)).equalsIgnoreCase(iTitleTextGetter.getTitle(list.get(i)))) {
                addHeaderToList(i, iTitleTextGetter.getTitle(list.get(i)));
            }
        }
        return headerList;
    }

    private void addHeaderToList(int index, String header) {
        headerList.put(index + adapterHeaderCount, header);
    }

    public interface ITitleTextGetter<T> {
        String getTitle(T obj);
    }
}

