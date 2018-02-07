package com.test.baserefreshview.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.test.baserefreshview.MainActivity;
import com.test.baserefreshview.PhotoSelectActivity;
import com.test.baserefreshview.R;
import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.base.XyBaseActivity;

/**
 * @author thisfeng
 * @date 2018/1/26-上午9:16
 */

public class HomeActivity extends XyBaseActivity implements View.OnClickListener {


    SimpleDraweeView sivPhoto;


    @Override
    protected int setActivityLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {

        findViewById(R.id.btnMain).setOnClickListener(this);
        findViewById(R.id.btnFile).setOnClickListener(this);
        findViewById(R.id.btnTakePhoto).setOnClickListener(this);
        findViewById(R.id.btnUCrop).setOnClickListener(this);
        findViewById(R.id.btnDrag).setOnClickListener(this);
        findViewById(R.id.btnDragSample).setOnClickListener(this);
        findViewById(R.id.btnGuide).setOnClickListener(this);
        sivPhoto = findViewById(R.id.sivPhoto);
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMain:
                start(MainActivity.class);
                break;
            case R.id.btnFile:
                start(FitAndroid7Activity.class);
                break;
            case R.id.btnTakePhoto:
                PhotoSelectActivity.startForResult(getThis(), PhotoSelectActivity.class, new PhotoSelectBaseActivity.CropParam());

//                PhotoSelectActivity.startForResult(getThis(), Const.cropParam, null, true);

                break;
            case R.id.btnUCrop:

//                UCrop.of(sourceUri, destinationUri)
//                        .withAspectRatio(16, 9)
//                        .withMaxResultSize(maxWidth, maxHeight)
//                        .start(context);
                break;
            case R.id.btnDrag:
                start(DragTestActivity.class);
                break;
            case R.id.btnDragSample:
                start(DragSampleActivity.class);
                break;
            case R.id.btnGuide:
                start(GuideActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPhotoSelectResult(int resultCode, Uri uri) {
        super.onPhotoSelectResult(resultCode, uri);
        if (resultCode == RESULT_OK) {
            String address = uri.getPath();
            sivPhoto.setImageURI(uri);
        }
    }
}
