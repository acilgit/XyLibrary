package com.test.baserefreshview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.yalantis.ucrop.UCrop;


public class PhotoSelectActivity extends PhotoSelectBaseActivity implements View.OnClickListener {


    Button btnCamera, btnAlbum, btnCancel;
    RelativeLayout rlMain;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_photo_select;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {

        findViewById(R.id.btnCamera).setOnClickListener(this);
        findViewById(R.id.btnAlbum).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.rlMain).setOnClickListener(this);
    }

    @Override
    protected boolean useEventBus() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera:
                onCamera();
                break;
            case R.id.btnAlbum:
                onAlbum();
                break;
            case R.id.btnCancel:
            case R.id.rlMain:
                finish();
                break;
        }
    }


    /**
     *
     * @return
     */

    @Override
    protected UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        return options;
    }


}
