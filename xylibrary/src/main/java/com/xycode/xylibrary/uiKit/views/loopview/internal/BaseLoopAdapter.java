package com.xycode.xylibrary.uiKit.views.loopview.internal;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;

import java.util.List;

/**
 *
 * BaseLoopAdapter
 *
 */
public abstract class BaseLoopAdapter extends PagerAdapter {

    protected Context context;
    protected List<String> loopData;
    protected OnItemClickListener onItemClickListener;
    protected ViewPager viewPager;
    /** holder */
    protected int defaultImgId;
    protected ScalingUtils.ScaleType imageScaleType = ScalingUtils.ScaleType.FIT_XY;
    protected float aspectRatio;
    protected BaseLoopView.OnPreviewUrlListener onPreviewUrlListener;

    public BaseLoopAdapter(Context context, List<String> loopData, ViewPager viewPager) {
        super();
        this.context = context;
        this.loopData = loopData;
        this.viewPager = viewPager;
        init();
    }

    protected void init() {
    }

    public void setLoopData(List<String> loopData) {
        this.loopData = loopData;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public ScalingUtils.ScaleType getImageScaleType() {
        return imageScaleType;
    }

    /**
     *
     * @param defaultImgId
     */
    public void setDefaultImgId(int defaultImgId) {
        this.defaultImgId = defaultImgId;
    }

    @Override
    public int getCount() {
        if(loopData.size() <= 1){
            return loopData.size();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view;
        final int index = position % loopData.size();
        String imageUrl = loopData.get(index);

        view = instantiateItemView(imageUrl, index);

        if(onItemClickListener != null) {
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(BaseLoopAdapter.this, view, index, position);
                }

            });
        }

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public abstract View instantiateItemView(String imageUrl, int position);

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setPreviewUrlListener(BaseLoopView.OnPreviewUrlListener listener) {
        onPreviewUrlListener = listener;
    }

    public void setImageScaleType(ScalingUtils.ScaleType imageScaleType) {
        this.imageScaleType = imageScaleType;
    }

    public interface OnItemClickListener {

        /**
         * PagerAdapter
         *
         * @param parent
         * @param view
         * @param position
         * @param realPosition
         */
        void onItemClick(PagerAdapter parent, View view, int position, int realPosition);
    }

}