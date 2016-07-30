package com.xycode.xylibrary.uiKit.views.banner;

/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class LoopViewPager extends ViewPager{

    private static final boolean DEFAULT_BOUNDARY_CASHING = false;

    ViewPager.OnPageChangeListener outerPageChangeListener;
    private LoopPagerAdapterWrapper adapter;
    private boolean boundaryCaching = DEFAULT_BOUNDARY_CASHING;
    private PagerAdapter adapterInner;
    
    public LoopViewPager(Context context) {
        this(context, null);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *   
     * @param position
     * @param count
     * @return (position-1)%count
     */
    public static int toRealPosition( int position, int count ){
        position = position-1;
        if( position < 0 ){
            position += count;
        }else{
            position = position%count;
        }
        return position;
    }
    
    /**
     * If set to true, the boundary views (i.e. first and last) will never be destroyed
     * This may help to prevent "blinking" of some views 
     * 
     * @param flag
     */
    public void setBoundaryCaching(boolean flag) {
        boundaryCaching = flag;
        if (adapter != null) {
            adapter.setBoundaryCaching(flag);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
    	if (adapter == null) return;
    	this.adapterInner = adapter;
        this.adapter = new LoopPagerAdapterWrapper(adapter);
        this.adapter.setBoundaryCaching(boundaryCaching);
        super.setAdapter(this.adapter);
        setCurrentItem(0, false);
    }
    
    @Override
    public PagerAdapter getAdapter() {
        return adapter != null ? adapter.getRealAdapter() : adapter;
    }

    @Override
    public int getCurrentItem() {
        return adapter != null ? adapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
    	if (adapter == null) adapter = new LoopPagerAdapterWrapper(adapterInner);
        int realItem = adapter.toInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        outerPageChangeListener = listener;
    };

    private void init() {
		try {
		    Field mScroller;
		    mScroller = ViewPager.class.getDeclaredField("mScroller");
		    mScroller.setAccessible(true); 
		    FixedSpeedScroller scroller = new FixedSpeedScroller(getContext());
		    mScroller.set(this, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
        super.addOnPageChangeListener(onPageChangeListener);
    }
    
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
        	if(adapter == null || adapter.getMAdapter() == null){
        		return;
        	}
            int realPosition = adapter.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (outerPageChangeListener != null) {
                    outerPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	if(adapter == null || adapter.getMAdapter() == null){
        		return;
        	}
            int realPosition = position;
            if (adapter != null) {
                realPosition = adapter.toRealPosition(position);
                if (positionOffset == 0
                        && mPreviousOffset == 0
                        && (position == 0 || position == adapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }

            mPreviousOffset = positionOffset;
            if (outerPageChangeListener != null && adapter != null && adapter.getRealAdapter() != null) {
                if (realPosition != adapter.getRealCount() - 1) {
                    outerPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        outerPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        outerPageChangeListener.onPageScrolled(realPosition, 0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        	if(adapter == null || adapter.getMAdapter() == null){
        		return;
        	}
            if (adapter != null) {
                int position = LoopViewPager.super.getCurrentItem();
                int realPosition = adapter.toRealPosition(position);
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == adapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }
            if (outerPageChangeListener != null) {
                outerPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

	
    
    private class FixedSpeedScroller extends Scroller {

    	private int mDuration = 300;

    	public FixedSpeedScroller(Context context) {
    		super(context, new DecelerateInterpolator());
    	}

    	@Override
    	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
    		// Ignore received duration, use fixed one instead
    		super.startScroll(startX, startY, dx, dy, mDuration);
    	}

    	@Override
    	public void startScroll(int startX, int startY, int dx, int dy) {
    		super.startScroll(startX, startY, dx, dy, mDuration);
    	}

    }
	
	
}