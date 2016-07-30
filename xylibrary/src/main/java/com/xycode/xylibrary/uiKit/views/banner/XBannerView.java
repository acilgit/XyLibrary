package com.xycode.xylibrary.uiKit.views.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class XBannerView extends RelativeLayout {

    private final ArrayList<SimpleDraweeView> imageViewList;

    private BannerBehavior bannerBehavior;

    private int att_placeHolder;
    private float att_aspectRatio = 2f;
    private long att_cutTime = 3000;

    private float INDICATOR_RATIO = 0.04f;

	private LoopViewPager viewPager;

	public LoopViewPager getViewPager() {
		return viewPager;
	}

	// FIXME when homepage banner more than 1 then add it
	private CirclePageIndicator indicator;
	private PagerAdapter mAdapter;
	private Handler cutHandler;
	private Runnable cutRunnable;
	private List<String> bannerUrls;
	private int cutIndex;
	private Context context;

	public XBannerView(Context context) {
		this(context, null);
	}

    public XBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context= context;
        imageViewList = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XBannerView);
        att_aspectRatio = typedArray.getFloat(R.styleable.XBannerView_aspectRatio, 16f / 9);
        att_cutTime = typedArray.getInt(R.styleable.XBannerView_aspectRatio, 3000);
        att_placeHolder = typedArray.getResourceId(R.styleable.XBannerView_holderImage, -1);
	}

    public void setup(List<String> urlList, BannerBehavior bannerBehavior) {
//        this.bannerUrls = urlList;
        this.bannerBehavior = bannerBehavior;
		createView();
		update(urlList);
    }

	public void update(@NonNull List urlList) {
		bannerUrls =  urlList;
		mAdapter = new BannerAdapter(urlList);
        viewPager.setAdapter(mAdapter);
		initCutHandler();
		return;
	}
	private int getScreenWidth(){
		return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
	}

	private void createView() {
		this.setBackgroundColor(0xFFFFFFFF);
//		int bannerHeight = (int)((getScreenWidth()* att_aspectRatio));
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.MATCH_PARENT;
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, );
		this.setLayoutParams(params);
		viewPager = new LoopViewPager(getContext());
		viewPager.setBoundaryCaching(true);
		this.addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int indicatorHeight = (int)(getScreenWidth() * INDICATOR_RATIO);
		indicator = new CirclePageIndicator(getContext());
		setIndicatorParams(indicator, indicatorHeight);
		indicator.setOnPageChangeListener(new BannerCutListener());
		LayoutParams indicatorParams = new LayoutParams(LayoutParams.MATCH_PARENT, indicatorHeight);
		indicatorParams.bottomMargin = 6;
		indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		this.addView(indicator, indicatorParams);
	}

	public void onDestroy() {
		mAdapter = null;
		viewPager.setAdapter(null);
	}

	private void initCutHandler() {
		if(cutHandler == null || cutRunnable == null) {
			cutHandler = new Handler();
			cutRunnable = new Runnable() {
				
				@Override
				public void run() {
					if (mAdapter == null || bannerUrls == null || bannerUrls.size() <= 0) {
						return;
					}
					if(cutIndex == mAdapter.getCount() - 1) {
						cutIndex = 0;
					} else{
						cutIndex += 1;
					}
					viewPager.setCurrentItem(cutIndex, true);
					cutHandler.removeCallbacks(this);
					cutHandler.postDelayed(this, att_cutTime);
				}
			};
		}
		cutHandler.removeCallbacks(cutRunnable);
		cutHandler.postDelayed(cutRunnable, att_cutTime);
	}
	
	private class BannerCutListener extends ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			super.onPageScrollStateChanged(state);
			if(cutHandler != null && cutRunnable != null) {
				if (ViewPager.SCROLL_STATE_DRAGGING == state) {
					cutHandler.removeCallbacks(cutRunnable);
				} else if (ViewPager.SCROLL_STATE_IDLE == state) {
					cutHandler.removeCallbacks(cutRunnable);
					cutHandler.postDelayed(cutRunnable, att_cutTime);
				}
			}
		}
		@Override
        public void onPageSelected(int position) {
			cutIndex = position;
        }
    }
	
	public void onDestroyHandler() {
		if(cutHandler != null && cutRunnable != null) {
			cutHandler.removeCallbacks(cutRunnable);
		}
	}
	
	public void onStartChange() {
		if(cutHandler != null && cutRunnable != null) {
			cutHandler.removeCallbacks(cutRunnable);
			cutHandler.postDelayed(cutRunnable, att_cutTime);
		}
	}
	
	protected void setIndicatorParams(CirclePageIndicator indicator, int indicatorHeight) {
		this.indicator.setPadding(0, indicatorHeight/4, 0, 0);
		this.indicator.setRadius(indicatorHeight/4);
		this.indicator.setPageColor(0x00FFFFFF);
		this.indicator.setFillColor(0xFFFFFFFF);
		this.indicator.setStrokeColor(0x66FFFFFF);
		this.indicator.setStrokeWidth(2);
		this.indicator.setSelectedRadius(indicatorHeight/4 + 1);
		indicator.setCentered(true);
	}
	
	private class BannerAdapter extends PagerAdapter {
		private List<String> dataList;
		
		protected BannerAdapter(List<String> dataList) {
			this.dataList = dataList;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
            SimpleDraweeView imageView = new SimpleDraweeView(getContext());
            imageView.setAspectRatio(att_aspectRatio);
           if(att_placeHolder!=-1) imageView.getHierarchy().setPlaceholderImage(att_placeHolder);
            String url = dataList.get(position);
            String urlPreview = "";
            if (bannerBehavior!=null) {
                urlPreview = bannerBehavior.getPreviewUrlFromPosition(url, position);
            }
            if (urlPreview.isEmpty()) {
                imageView.setImageURI(Uri.parse(url));
            } else {
                ImageUtils.setImageUriWithPreview(imageView, url, urlPreview);
            }
			container.addView(imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    if (bannerBehavior != null) {
                        bannerBehavior.bannerClickListener.onClick(v, position);
                    }
                }
			});
            return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}
	}

}
