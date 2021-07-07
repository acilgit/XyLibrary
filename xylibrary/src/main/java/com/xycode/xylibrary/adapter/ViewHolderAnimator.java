package com.xycode.xylibrary.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by XY on 2017-05-22.
 *
 */
 class ViewHolderAnimator {
    public static class ViewHolderAnimatorListener extends AnimatorListenerAdapter {
        private final RecyclerView.ViewHolder holder;

        public ViewHolderAnimatorListener(RecyclerView.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            holder.setIsRecyclable(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            holder.setIsRecyclable(true);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            holder.setIsRecyclable(true);
        }
    }

    public static class LayoutParamsAnimatorListener extends AnimatorListenerAdapter {
        private final View view;
        private final int paramsWidth;
        private final int paramsHeight;

        public LayoutParamsAnimatorListener(View view, int paramsWidth, int paramsHeight) {
            this.view = view;
            this.paramsWidth = paramsWidth;
            this.paramsHeight = paramsHeight;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            final ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = paramsWidth;
            params.height = paramsHeight;
            view.setLayoutParams(params);
        }
    }

    public static Animator ofItemViewHeight(RecyclerView.ViewHolder holder) {
        View parent = (View) holder.itemView.getParent();
        if (parent == null)
            throw new IllegalStateException("Cannot animate the layout of a view that has no parent");

        int start = holder.itemView.getMeasuredHeight();
        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int end = holder.itemView.getMeasuredHeight();

        final Animator animator = ofHeight(holder.itemView, start, end);
        animator.addListener(new ViewHolderAnimatorListener(holder));

        animator.addListener(new LayoutParamsAnimatorListener(holder.itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return animator;
    }

    private static class LayoutHeightUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private final View view;

        LayoutHeightUpdateListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height = (int) animation.getAnimatedValue();
            view.setLayoutParams(lp);
        }

    }

    private static Animator ofHeight(View view, int start, int end) {
        final ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new LayoutHeightUpdateListener(view));
        return animator;
    }
}
