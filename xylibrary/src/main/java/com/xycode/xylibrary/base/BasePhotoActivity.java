package com.xycode.xylibrary.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.views.NoScrollViewPager;
import com.xycode.xylibrary.uiKit.views.TouchImageView;
import com.xycode.xylibrary.unit.UrlData;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.ShareStorage;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePhotoActivity extends BaseActivity {

    private static final String photos = "photos";
    private static final String position = "position";
    View.OnLongClickListener longClickListener;

    public static void startThis(BaseActivity activity, Class photoActivityClass, String url) {
        List<UrlData> urls = new ArrayList<>();
        urls.add(new UrlData(url));
        startThis(activity, photoActivityClass, urls, 0);
    }

    public static void startThis(BaseActivity activity,  Class photoActivityClass, List<UrlData> urls, int pos) {
//        getPhotoStorage(activity).put(photos, JSON.toJSONString(urls));
        activity.startActivity(new Intent(activity, photoActivityClass).putExtra(photos, JSON.toJSONString(urls)).putExtra(position, pos));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_photo);
        NoScrollViewPager vpMain = (NoScrollViewPager) findViewById(R.id.vpMain);
        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.rlMain);

        int pos = getIntent().getIntExtra(position, 0);
        try {
            List<UrlData> urlDatas = JSON.parseArray(getIntent().getStringExtra(photos), UrlData.class);
            boolean singlePhoto = urlDatas.size() == 1;
            PhotoPagerAdapter fragmentAdapter = new PhotoPagerAdapter(this, urlDatas);
            vpMain.setScrollable(!singlePhoto);
            vpMain.setAdapter(fragmentAdapter);
            vpMain.setOffscreenPageLimit(1);
            vpMain.setCurrentItem(pos);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected abstract View addPageView(ViewGroup container, UrlData data);

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    class PhotoPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private BaseActivity context;
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
                ImageUtils.loadBitmapFromFresco(context, Uri.parse(data.getUrl()), bitmap -> runOnUiThread(() -> {
                    float pRatio = (bitmap.getHeight() * 1.0f) / bitmap.getWidth();
                    float ratio = pRatio / 1.6f;
                    ivPhoto.setMaxZoom((ratio > 1 ? ratio * 3 : 3));
                    ivPhoto.setImageBitmap(bitmap);
                }));
            }
            container.addView(currentView);
            View addPageView = addPageView(container, data);
            if(addPageView != null) container.addView(addPageView);

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
