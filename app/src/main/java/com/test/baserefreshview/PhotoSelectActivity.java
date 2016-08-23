package com.test.baserefreshview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.utils.TS;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoSelectActivity extends PhotoSelectBaseActivity {

    @Bind(R.id.btnCamera)
    Button btnCamera;
    @Bind(R.id.btnAlbum)
    Button btnAlbum;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.rlMain)
    RelativeLayout rlMain;

    public static void startForResult(Activity activity, boolean isCrop) {
        activity.startActivityForResult(new Intent(activity, PhotoSelectActivity.class).putExtra(IS_CROP, isCrop), REQUEST_CODE_PHOTO_SELECT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnCamera, R.id.btnAlbum, R.id.btnCancel, R.id.rlMain})
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
}
