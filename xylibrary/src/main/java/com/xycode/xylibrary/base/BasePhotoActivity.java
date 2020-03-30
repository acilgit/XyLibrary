package com.xycode.xylibrary.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.views.NoScrollViewPager;
import com.xycode.xylibrary.uiKit.views.TouchImageView;
import com.xycode.xylibrary.unit.UrlData;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePhotoActivity extends XyBaseActivity {

    private static final String photos = "photos";
    private static final String position = "position";
    private static XyBaseActivity activity;
    View.OnLongClickListener longClickListener;
    private LinearLayout llIndexContainer;
    private NoScrollViewPager vpMain;
    private int pos;
    private Options options;

    public static void startThis(XyBaseActivity activity, Class photoActivityClass, String url) {
        List<UrlData> urls = new ArrayList<>();
        urls.add(new UrlData(url));
        BasePhotoActivity.activity = activity;
        startThis(activity, photoActivityClass, urls, 0);
    }

    public static void startThis(XyBaseActivity activity, Class photoActivityClass, List<UrlData> urls, int pos) {
//        getPhotoStorage(activity).put(photos, JSON.toJSONString(urls));
        activity.startActivity(new Intent(activity, photoActivityClass).putExtra(photos, JSON.toJSONString(urls)).putExtra(position, pos));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //remove ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_base_photo;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {
        options = setDisplayOptions();
        vpMain = (NoScrollViewPager) findViewById(R.id.vpMain);
        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        llIndexContainer = (LinearLayout) findViewById(R.id.ll_index_container);

        if (options.backgroundColor != 0) rlMain.setBackgroundResource(options.backgroundColor);

        pos = getIntent().getIntExtra(position, 0);
        try {
            List<UrlData> urlDatas = JSON.parseArray(getIntent().getStringExtra(photos), UrlData.class);
            boolean singlePhoto = urlDatas.size() == 1;
            PhotoPagerAdapter fragmentAdapter = new PhotoPagerAdapter(this, urlDatas);
            vpMain.setScrollable(!singlePhoto);
            vpMain.setAdapter(fragmentAdapter);
            vpMain.setOffscreenPageLimit(1);
            vpMain.setCurrentItem(pos);
            if (options.isShowDotsView() && urlDatas.size() > 1) {
                updateIndicatorView(urlDatas.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected abstract View addPageView(ViewGroup container, UrlData data);

   /* protected boolean isShowDotsView() {
        return false;
    }*/

    protected Options setDisplayOptions() {
        return new Options();
    }

    public class Options {
        boolean isShowDotsView = false;
        int bitmapFailureRes = 0;
        int backgroundColor = 0;

        public boolean isShowDotsView() {
            return isShowDotsView;
        }

        public void setShowDotsView(boolean showDotsView) {
            isShowDotsView = showDotsView;
        }

        public int getBitmapFailureRes() {
            return bitmapFailureRes;
        }

        public void setBitmapFailureRes(int bitmapFailureRes) {
            this.bitmapFailureRes = bitmapFailureRes;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    /**
     * add Indicator Method ,Afferent Datas size
     *
     * @param size
     */
    public void updateIndicatorView(int size) {
        addIndicatorImageViews(size);
        setViewPagerChangeListener(size);
    }


    private void addIndicatorImageViews(int size) {
        llIndexContainer.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(getBaseContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Tools.dp2px( 5), Tools.dp2px( 5));
            if (i != 0) {
                lp.leftMargin = Tools.dp2px( 7);
            }
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.drawable.abc_background_indicator);
            iv.setEnabled(i == pos);
            llIndexContainer.addView(iv);
        }
    }

    private void setViewPagerChangeListener(final int size) {
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (size > 0) {
                    int newPosition = position % size;
                    for (int i = 0; i < size; i++) {
                        llIndexContainer.getChildAt(i).setEnabled(false);
                        if (i == newPosition) {
                            llIndexContainer.getChildAt(i).setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }


    class PhotoPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private XyBaseActivity context;
        private List<UrlData> dataList;

        PhotoPagerAdapter(BasePhotoActivity context, List<UrlData> dataList) {
            this.context = context;
            this.dataList = dataList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RelativeLayout currentView;
            currentView = (RelativeLayout) inflater.inflate(R.layout.layout_base_photo_zoom, container, false);
            final TouchImageView ivPhoto = (TouchImageView) currentView.findViewById(R.id.ivPhoto);
            final UrlData data = dataList.get(position);

            ivPhoto.setOnClickListener(v -> context.finish());
            ivPhoto.setOnLongClickListener(longClickListener);
            if (data.getUrl() != null) {
                ImageUtils.loadBitmapFromFresco(Uri.parse((String) data.getUrl()), bitmap -> runOnUiThread(() -> {
                    if (bitmap != null) {
                        float pRatio = (bitmap.getHeight() * 1.0f) / bitmap.getWidth();
                        float ratio = pRatio / 1.6f;
                        ivPhoto.setMaxZoom((ratio > 1 ? ratio * 3 : 3));
                        ivPhoto.setImageBitmap(bitmap);
                    } else {
                        if (options.bitmapFailureRes != 0)
                            ivPhoto.setImageResource(options.bitmapFailureRes);
                    }
                }));
            }
            container.addView(currentView);
            View addPageView = addPageView(container, data);
            if (addPageView != null) container.addView(addPageView);

            return currentView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

}
