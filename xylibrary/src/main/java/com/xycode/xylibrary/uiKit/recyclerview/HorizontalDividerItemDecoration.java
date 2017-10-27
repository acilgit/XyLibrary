package com.xycode.xylibrary.uiKit.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.xycode.xylibrary.adapter.XAdapter;

/**
 * Created by yqritc on 2015/01/15.
 */
public class HorizontalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;
    private GapProvider gapProvider;

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
        gapProvider = builder.gapProvider;
        if (gapProvider == null) {
            gapProvider = new GapProvider() {
                @Override
                public int gapLeft(int position, RecyclerView parent) {
                    return 0;
                }

                @Override
                public int gapRight(int position, RecyclerView parent) {
                    return 0;
                }

                @Override
                public int gapWidth(int position, RecyclerView parent) {
                    return 0;
                }
            };
        }
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mMarginProvider.dividerRightMargin(position, parent) + transitionX;

        int dividerSize = getDividerSize(position, parent);
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.top = child.getBottom() + params.topMargin + transitionY;
            bounds.bottom = bounds.top + dividerSize;
        } else {
            bounds.top = child.getBottom() + params.topMargin + dividerSize / 2 + transitionY;
            bounds.bottom = bounds.top;
        }

        return bounds;
    }

    /**
     * draw gap
     * @param outRect
     * @param v
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View v, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null) {
            if (parent.getLayoutManager() instanceof LinearLayoutManager) {
                if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
//                    right = gapProvider.gapRight(position, parent);
                }
            } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {

                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
                int spanIndex = lp.getSpanIndex();

                if (parent.getAdapter() instanceof XAdapter) {
                    int pos = parent.getChildAdapterPosition(v);
                    XAdapter adapter = (XAdapter) parent.getAdapter();
                    int firstItemPos = adapter.getHeaderCount() - 1;
                    int lastItemPos = adapter.getHeaderCount() - 1 + adapter.getShowingList().size();
                    if (!lp.isFullSpan() && pos > firstItemPos && pos <= lastItemPos) {
                        if (spanIndex == 1) {
                            outRect.left = gapProvider.gapWidth(pos, parent);
                        } else {
                            outRect.right = gapProvider.gapWidth(pos, parent);
                        }
                    }
                }
            }
            super.getItemOffsets(outRect, v, parent, state);
        }

    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
//        int left = 0, right = 0;
//        outRect.set(left, 0, right, getDividerSize(position, parent));
        outRect.bottom = getDividerSize(position, parent);
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return left margin
         */
        int dividerLeftMargin(int position, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return right margin
         */
        int dividerRightMargin(int position, RecyclerView parent);
    }

    /**
     * 取得横行间隔
     */
    public interface GapProvider {
        int gapLeft(int position, RecyclerView parent);

        int gapRight(int position, RecyclerView parent);

        int gapWidth(int position, RecyclerView parent);


    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        private GapProvider gapProvider;

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }

        public Builder setGapProvider(GapProvider gapProvider) {
            this.gapProvider = gapProvider;
            return this;
        }
    }
}