package com.xycode.xylibrary.uiKit.views.loopview.internal;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;

import java.util.List;

/**
 * BaseLoopView
 *
 */
public abstract class BaseLoopView extends RelativeLayout implements ILoopView {

    private final ScalingUtils.ScaleType actualScale;
    protected float aspectRatio;
    /** ViewPager */
    protected ViewPager viewPager;
    /** customer LayoutId */
    protected int loopLayoutId;
    /** ViewPager Adapter */
    private BaseLoopAdapter adapter;

    protected LinearLayout dotsView;
    /** indicator position */
    protected int currentPosition = -1;
    protected float dotMargin;
    /** cut time */
    protected long interval;
    /** indicator selector */
    protected int dotSelector;
    /** holder image */
    protected int defaultImgId;
    private boolean autoLoop = true;

    private boolean stopScrollWhenTouch = true;
    /** stop looping when touched */
    private boolean isStoppedByTouch = false;
    /** when invisible stop */
    private boolean isStoppedByInvisible = false;
    /** current loop state */
    private boolean isAutoScroll = true;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    /** loop direction */
    protected int direction = RIGHT;

    protected List<String> loopData;

    private Handler handler;
    /** onClick listener */
    protected BaseLoopAdapter.OnItemClickListener onItemClickListener;
    /** auto jump callback */
    protected OnLoopListener onLoopListener;
    /** slide controller */
    private LoopViewScroller scroller;

    private OnPreviewUrlListener onPreviewUrlListener;

    private float downX;
    private float downY;

    public BaseLoopView(Context context) {
        this(context, null);
    }

    public BaseLoopView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BaseLoopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final float defaultDotMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        final int defaultInterval = 4000;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopView);

        dotMargin = a.getDimension(R.styleable.LoopView_loopDotMargin, defaultDotMargin);
        autoLoop = a.getBoolean(R.styleable.LoopView_loopAutoLoop, true);
        dotSelector = a.getResourceId(R.styleable.LoopView_loopDotSelector, R.drawable.loop_view_dots_selector);
        loopLayoutId = a.getResourceId(R.styleable.LoopView_loopLayout, 0);
        aspectRatio = a.getFloat(R.styleable.LoopView_aspectRatio, 0);
        interval = a.getInt(R.styleable.LoopView_loopCutTime, defaultInterval);
        defaultImgId = a.getResourceId(R.styleable.LoopView_holderImage, 0);
        int intActualScale = a.getInt(R.styleable.LoopView_imageScaleType, 0);
        actualScale = ImageUtils.checkFrescoScaleType(intActualScale);
        a.recycle();

        initRealView();
    }

    protected void setViewListener() {

        setOnPageChangeListener();

        adapter.setOnItemClickListener(new BaseLoopAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(PagerAdapter parent, View view, int position, int realPosition) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position, realPosition);
                }
            }
        });

        adapter.setPreviewUrlListener(onPreviewUrlListener);
        adapter.setImageScaleType(actualScale);
    }

    public void setPreviewUrlListener(OnPreviewUrlListener onPreviewUrlListener) {
        this.onPreviewUrlListener = onPreviewUrlListener;
    }

    /**
     *
     * @param duration
     */
    @Override
    public void setScrollDuration(long duration) {
        scroller = new LoopViewScroller(getContext());
        scroller.setScrollDuration(duration);
        scroller.initViewPagerScroll(viewPager);
    }

    @Override
    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public List<String> getLoopData() {
        return loopData;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void initData(List<String> loopData) {
        if (null == loopData) return;
        this.loopData = loopData;

        initLoopViewPager();
    }

    public void refreshData(List<String> loopData) {
        if (null == loopData) return;
        stopAutoLoop();
        removeAllViews();
        initRealView();
        this.loopData = null;
        this.loopData = loopData;
        initLoopViewPager();
        invalidate();
    }

    private void initLoopViewPager() {
        adapter = initAdapter();
        adapter.setDefaultImgId(defaultImgId);
        adapter.setAspectRatio(aspectRatio);
        viewPager.setAdapter(adapter);
        initDots(loopData.size());

        setViewListener();
        if (loopData.size() > 0) {
            int startPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % loopData.size();
            viewPager.setCurrentItem(startPosition, false);
            if (handler == null) {
                handler = new LoopHandler(this, (Activity)getContext());
            }

            if (autoLoop) {
                startAutoLoop();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(ev.getY() - downY) > Math.abs(ev.getX() - downX)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * stopScrollWhenTouch TRUE, stop
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(stopScrollWhenTouch) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(isAutoScroll) {
                        stopAutoLoop();
                        isStoppedByTouch = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(isStoppedByTouch) {
                        startAutoLoop(interval);
                        isStoppedByTouch = false;
                    }
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        switch (visibility) {
            case VISIBLE:
                if(isStoppedByInvisible) {
                    startCurrentAutoLoop();
                    isStoppedByInvisible = false;
                }
                break;
            case INVISIBLE:
            case GONE:
                if(isAutoScroll) {
                    stopAutoLoop();
                    isStoppedByInvisible = true;
                }
                break;
        }
    }

    public void startAutoLoop() {
        startAutoLoop(interval);
    }

    /**
     *
     * @param delayTimeInMills
     */
    public void startAutoLoop(long delayTimeInMills) {
        if (null == loopData || loopData.size() <= 1) return;
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }

    /**
     *
     * @param delayTimeInMills
     */
    public void sendScrollMessage(long delayTimeInMills) {
        removeAllMessages();
        handler.sendEmptyMessageDelayed(0, delayTimeInMills);
    }

    public void startCurrentAutoLoop() {
        if (null == loopData || loopData.size() <= 1) return;
        isAutoScroll = true;
        removeAllMessages();
        handler.sendEmptyMessage(1);
    }

    public void removeAllMessages() {
        if(null != handler) {
            handler.removeMessages(0);
            handler.removeMessages(1);
        }
    }

    @Override
    public void stopAutoLoop() {
        isAutoScroll = false;
        if (handler != null) {
            removeAllMessages();
        }
    }

    public boolean isAutoScroll() {
        return isAutoScroll;
    }


    public int getDirection() {
        return direction;
    }

    public void setOnImageClickListener(BaseLoopAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnLoopListener(OnLoopListener l) {
        this.onLoopListener = l;
    }

    protected abstract void initRealView();

    protected abstract BaseLoopAdapter initAdapter();

    protected abstract void initDots(int size);

    protected abstract void setOnPageChangeListener();

    /**
     * OnLoopListener
     *
     * while Adapter clicked callback
     */
    public interface OnLoopListener {

        /**
         * LoopView when jump to the first one callback
         *
         * @param realPosition
         */
        void onLoopToStart(int realPosition);

        /**
         * LoopView when jump to the last one callback
         *
         * @param realPosition
         */
        void onLoopToEnd(int realPosition);
    }

    public interface OnPreviewUrlListener {
        String getPreviewUrl(String url, int position);
    }


}