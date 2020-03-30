package com.test.baserefreshview;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.uiKit.views.MultiImageView;
import com.xycode.xylibrary.unit.UrlData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thisfeng on 2016/11/24 0024.
 */
public class PhotoDemoActivity extends XyBaseActivity {

    @Override
    protected boolean useEventBus() {
        return false;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_photo_demo;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {

        MultiImageView multiImageView = findViewById(R.id.multiView);

        List<UrlData> images = new ArrayList<>();
        images.add(new UrlData("res:///" + R.mipmap.aa));
        images.add(new UrlData("res:///" + R.mipmap.bbb));
//        images.add(new UrlData("res:///" + R.mipmap.ic_launcher));
//        images.add(new UrlData("res:///" + R.mipmap.ic_camera_g));
        multiImageView.setList(images);
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, UrlData urlData) {
                PhotoActivity.startThis(getThis(), images, position);
            }
        });
    }
}
