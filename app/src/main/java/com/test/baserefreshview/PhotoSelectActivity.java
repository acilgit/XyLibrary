package com.test.baserefreshview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.utils.TS;
import com.yalantis.ucrop.UCrop;

//import butterknife.Bind;

public class PhotoSelectActivity extends PhotoSelectBaseActivity {

//    @BindView(R.id.btnCamera)
//    Button btnCamera;
//    @BindView(R.id.btnAlbum)
//    Button btnAlbum;
//    @BindView(R.id.btnCancel)
//    Button btnCancel;
//    @BindView(R.id.rlMain)
//    RelativeLayout rlMain;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_photo_select;
    }

    @Override
    protected void initOnCreate() {

    }

    @Override
    protected boolean useEventBus() {
        return false;
    }

//    @OnClick({R.id.btnCamera, R.id.btnAlbum, R.id.btnCancel, R.id.rlMain})
    public void onClick(View view) {
        switch (view.getId()) {
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

    @Override
    protected void permissionOnDenied(String permission) {
        TS.show("no " + permission);
    }

    @Override
    protected UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        return options;
    }
}
