package com.xycode.xylibrary.uiKit.views.loopview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.views.loopview.internal.BaseLoopAdapter;
import com.xycode.xylibrary.uiKit.views.loopview.internal.BaseLoopView;
import com.xycode.xylibrary.utils.Tools;

/**
 *
 * AdLoopView
 *
 */
public class AdLoopView extends BaseLoopView {

    public AdLoopView(Context context) {
        this(context, null);
    }

    public AdLoopView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AdLoopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the custom layout to be inflated for the loop views.
     *
     * @param layoutResId Layout id to be inflated
     */
    public void setLoopLayout(int layoutResId) {
        loopLayoutId = layoutResId;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void initRealView() {
        View view = null;
        if (loopLayoutId != 0) {
            // If there is a custom loop view layout id set, try and inflate it
            view = LayoutInflater.from(getContext()).inflate(loopLayoutId, null);
            setCustomerLayout(view);
            // ViewPager
            viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            // indicator view
            dotsView = (LinearLayout) view.findViewById(R.id.dots);
//            descText = (TextView) view.findViewById(com.kevin.loopview.R.id.loop_view_desc);
        }

        if(view == null) {
            view = createDefaultView();
        }
        setScrollDuration(1000);
        this.addView(view);
    }

    private View createDefaultView() {
        RelativeLayout contentView = new RelativeLayout(getContext());
        int viewWidth = ViewGroup.LayoutParams.MATCH_PARENT;
//        int viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int viewHeight = (int) (1.0f *Tools.getScreenSize(getContext()).x / aspectRatio);
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(viewWidth, viewHeight);
        contentView.setLayoutParams(viewParams);
        // 初始化ViewPager
        viewPager = new ViewPager(getContext());
        viewPager.setId(R.id.viewPager);
        int viewPagerWidth = LayoutParams.MATCH_PARENT;
//        int viewPagerHeight = LayoutParams.MATCH_PARENT;
        int viewPagerHeight = viewHeight;
        LayoutParams viewPagerParams = new LayoutParams(viewPagerWidth, viewPagerHeight);
        this.addView(viewPager, viewPagerParams);
        // init Layout
        RelativeLayout bottomLayout = new RelativeLayout(getContext());
        int bottomLayoutWidth =  LayoutParams.MATCH_PARENT;
        int bottomLayoutHeight =  LayoutParams.WRAP_CONTENT;
        LayoutParams bottomLayoutParams = new LayoutParams(bottomLayoutWidth, bottomLayoutHeight);
        bottomLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, viewPager.getId());
        Drawable mBackground = new ColorDrawable(Color.DKGRAY);
        mBackground.setAlpha((int) (0.3 * 255));
//        bottomLayout.setBackgroundDrawable(mBackground);
        bottomLayout.setGravity(Gravity.CENTER);
        this.addView(bottomLayout, bottomLayoutParams);
        // init indicator
        dotsView = new LinearLayout(getContext());
        dotsView.setId(R.id.dots);
        int dotsViewWidth = LayoutParams.WRAP_CONTENT;
        int dotsViewHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams dotsViewParams = new LayoutParams(dotsViewWidth, dotsViewHeight);
        dotsView.setOrientation(LinearLayout.HORIZONTAL);
        dotsViewParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        dotsViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bottomLayout.addView(dotsView, dotsViewParams);
     /*   descText = new TextView(getContext());
        int descTextWidth = LayoutParams.MATCH_PARENT;
        int descTextHeight = LayoutParams.WRAP_CONTENT;
        LayoutParams descTextParams = new LayoutParams(descTextWidth, descTextHeight);
        descTextParams.addRule(RelativeLayout.LEFT_OF, dotsView.getId());
        descText.setSingleLine(true);
        descText.getPaint().setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        descText.setTextColor(Color.WHITE);
        descText.setGravity(Gravity.LEFT);
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        descText.setPadding(padding, padding, padding, padding);
        bottomLayout.addView(descText, descTextParams);*/

        return contentView;
    }

    protected void setCustomerLayout(View view) {
    }

    @Override
    protected BaseLoopAdapter initAdapter() {
        return new AdLoopAdapter(getContext(), loopData, viewPager);
    }

    @Override
    protected void initDots(int size) {
        if(null != dotsView) {
            dotsView.removeAllViews();
            for(int i=0; i<size; i++){
                ImageView dot = new ImageView(getContext());
                dot.setBackgroundResource(dotSelector);
                int dotWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
                int dotHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
                LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dotWidth, dotHeight);
                dotParams.setMargins(0, (int) dotMargin, (int) dotMargin, (int) dotMargin);
                if(i == 0){
                    dot.setEnabled(true);
                }else{
                    dot.setEnabled(false);
                }
                dotsView.addView(dot, dotParams);
            }
        }
    }

    @Override
    protected void setOnPageChangeListener() {
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int i = position % loopData.size();
                if(null != dotsView) {
                    dotsView.getChildAt(i).setEnabled(true);
                }
                if(null != dotsView && currentPosition != -1) {
                    dotsView.getChildAt(currentPosition).setEnabled(false);
                }
                currentPosition = i;
               /* if(null != descText) {
                    if(!TextUtils.isEmpty(loopData.items.get(i).descText)) {
                        if(descText.getVisibility() != View.VISIBLE)
                            descText.setVisibility(View.VISIBLE);
                        String imageDesc = loopData.items.get(i).descText;
                        descText.setText(imageDesc);
                    } else {
                        if(descText.getVisibility() == View.VISIBLE)
                            descText.setVisibility(View.GONE);
                    }
                }*/

                if(onLoopListener != null) {
                    if(i == 0) {
                        onLoopListener.onLoopToStart(position);
                    } else if(i == loopData.size() -1) {
                        onLoopListener.onLoopToEnd(position);
                    }
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

}