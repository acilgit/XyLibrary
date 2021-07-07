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
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.xycode.xylibrary.adapter.CustomHolder;
import com.xycode.xylibrary.adapter.XAdapter;

import java.util.Map;

/**
 * 只能被 XAdapter 使用
 * @param <T>
 */
public class PinningBarItemDecoration<T> extends RecyclerView.ItemDecoration {

    private Context context;
    private int mTitleHeight;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mTextHeight;
    private int mTextBaselineOffset;
    private int mTextStartMargin;
    private int adapterHeaderCount = 0;
    private RecyclerView parent;
    private XAdapter adapter;

    private ITitleTextGetter iTitleTextGetter;

    /**
     * Integer means the related position of the RecyclerView#getViewAdapterPosition()
     * (the position of the view in original adapter's list)
     * String means the title to be drawn
     */
    private Map<Integer, String> headerList;
    private View pinnedHeaderView;
    private Rect clipBounds;
    private int pinnedHeaderTop;

  /*  public PinningBarItemDecoration(RecyclerView parent, List sourceList, Options options, ITitleTextGetter iTitleTextGetter) {
        this.parent = parent;
        this.context = parent.getContext();
        if (parent.getAdapter() instanceof XAdapter) {
            this.adapter = (XAdapter) parent.getAdapter();
            RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    if (adapter != null) {
                        setList(adapter.getShowingList(), adapter.getHeaderCount());
                    }
                }
            };
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }
        Resources resources = context.getResources();
        this.iTitleTextGetter = iTitleTextGetter;
        this.headerList = preHeaderList(sourceList);
        this.mTitleHeight = resources.getDimensionPixelSize(options.titleHeight);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(context, options.backgroundColorRes));

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(context, options.textColor));
        mTextPaint.setTextSize(context.getResources().getDimensionPixelSize(options.textSize));

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
//        super.onDraw(c, parent, state);
        createPinnedHeader(parent);
        if (pinnedHeaderView != null) {
            int headerEndAt = pinnedHeaderView.getTop() + pinnedHeaderView.getHeight();
            View v = parent.findChildViewUnder(c.getWidth() / 2, headerEndAt + 1);

            if (isPinnedView(parent, v)) {
                pinnedHeaderTop = v.getTop() - pinnedHeaderView.getHeight();
            } else {
                pinnedHeaderTop = 0;
            }

            clipBounds = c.getClipBounds();
            clipBounds.top = pinnedHeaderTop + pinnedHeaderView.getHeight();
            c.clipRect(clipBounds);
        }

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

    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        final int rectBottom = child.getTop() - params.topMargin;
        c.drawRect(left, rectBottom - mTitleHeight, right,
                rectBottom, mBackgroundPaint);
        c.drawText(headerList.get(position), child.getPaddingLeft() + mTextStartMargin,
                rectBottom - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);
    }
    private void createPinnedHeader(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null || layoutManager.getChildCount() <= 0) {
            return;
        }
        int firstVisiblePosition = ((RecyclerView.LayoutParams) layoutManager.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
        int headerPosition = findPinnedHeaderPosition(parent, firstVisiblePosition);

        if (headerPosition >= 0 && mHeaderPosition != headerPosition) {
            mHeaderPosition = headerPosition;
            int viewType = mAdapter.getItemViewType(headerPosition);

            RecyclerView.ViewHolder pinnedViewHolder = mAdapter.createViewHolder(parent, viewType);
            mAdapter.bindViewHolder(pinnedViewHolder, headerPosition);
            mPinnedHeaderView = pinnedViewHolder.itemView;

            // read layout parameters
            ViewGroup.LayoutParams layoutParams = mPinnedHeaderView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPinnedHeaderView.setLayoutParams(layoutParams);
            }

            int heightMode = View.MeasureSpec.getMode(layoutParams.height);
            int heightSize = View.MeasureSpec.getSize(layoutParams.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            if (heightSize > maxHeight) {
                heightSize = maxHeight;
            }

            // measure & layout
            int ws = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
            int hs = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            mPinnedHeaderView.measure(ws, hs);
            mPinnedHeaderView.layout(0, 0, mPinnedHeaderView.getMeasuredWidth(), mPinnedHeaderView.getMeasuredHeight());
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (pinnedHeaderView != null) {
            c.save();
            clipBounds.top = 0;
            c.clipRect(clipBounds, Region.Op.UNION);
            c.translate(0, pinnedHeaderTop);
            pinnedHeaderView.draw(c);

            c.restore();
       *//* super.onDrawOver(c, parent, state);
        final int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        View child = parent.findViewHolderForAdapterPosition(position).itemView;
        String initial = getTag(position);
        if (initial == null) {
            return;
        }

        boolean flag = false;
        if (getTag(position + 1) != null && !initial.equals(getTag(position + 1))) {
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
        }*//*
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

    *//**
     * 设置列表
     *
     * @param list               数据源
     * @param adapterHeaderCount 适配器中的自定义的Header的数量
     *//*
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

        *//**
         * 设置标题栏的背景颜色
         *
         * @param backgroundColorRes
         * @return
         *//*
        public Options setBackgroundColor(@ColorRes int backgroundColorRes) {
            this.backgroundColorRes = backgroundColorRes;
            return this;
        }

        *//**
         * 设置背景栏字体颜色、大小
         *
         * @param textColor
         * @param textSize
         * @return
         *//*
        public Options setTextPaint(@ColorRes int textColor, @DimenRes int textSize) {
            this.textColor = textColor;
            this.textSize = textSize;
            return this;
        }

        *//**
         * 设置标题左偏移量
         *
         * @param textStartMargin
         * @return
         *//*
        public Options setTextStartMargin(@DimenRes int textStartMargin) {
            this.textStartMargin = textStartMargin;
            return this;
        }
    }

    *//**
     * calculate the TAG and add to {@Link #mHeaderlist}
     *//*
    private Map<Integer, String> preHeaderList(List list) {
        if (headerList == null) {
            headerList = new LinkedHashMap<>();
        } else {
            headerList.clear();
        }
        if (list == null || list.size() == 0 || iTitleTextGetter == null) {
            return headerList;
        }
        addHeaderToList(adapterHeaderCount, iTitleTextGetter.getTitle(list.get(0)));
        for (int i = 2; i < list.size(); i++) {
            if (!iTitleTextGetter.getTitle(list.get(i - 1)).equalsIgnoreCase(iTitleTextGetter.getTitle(list.get(i)))) {
                addHeaderToList(i + adapterHeaderCount, iTitleTextGetter.getTitle(list.get(i)));
            }
        }
        return headerList;
    }

    private void addHeaderToList(int index, String header) {
        headerList.put(index, header);
    }
*/
    public interface ITitleTextGetter<T> {
        String getTitle(CustomHolder holder, T obj);
    }
}

