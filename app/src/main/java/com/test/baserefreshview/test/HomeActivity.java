package com.test.baserefreshview.test;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.test.baserefreshview.MainActivity;
import com.test.baserefreshview.PhotoSelectActivity;
import com.test.baserefreshview.R;
import com.xycode.xylibrary.base.PhotoSelectBaseActivity;
import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.okHttp.OkResponseListener;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.takephoto.model.CropOptions;
import com.xycode.xylibrary.takephoto.model.TImage;
import com.xycode.xylibrary.utils.LogUtil.L;
import com.xycode.xylibrary.utils.toast.TS;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

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
        findViewById(R.id.btnDragTest).setOnClickListener(this);
        findViewById(R.id.btnDragSample).setOnClickListener(this);
        findViewById(R.id.btnTest).setOnClickListener(this);
        findViewById(R.id.btnGuide).setOnClickListener(this);
        findViewById(R.id.btnCrash).setOnClickListener(v -> {
            // 在这里模拟异常抛出情况，人为抛出一个运行时异常
            throw new RuntimeException("自定义异常：这是自己抛出的异常aaaaaa");
        });
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
                CropOptions options = new CropOptions.Builder().create();
                options.setCrop(false);
                PhotoSelectActivity.startForResult(getThis(), PhotoSelectActivity.class, new PhotoSelectBaseActivity.PhotoParam(true)
                        , options);

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

                case R.id.btnDragTest:
                start(DragTest2Activity.class);
                break;
            case R.id.btnDragSample:
                start(DragSampleActivity.class);
                break;
            case R.id.btnGuide:
                start(GuideActivity.class);
                break;
            case R.id.btnTest:

                int b = 0;
                int a = 1 / b;

                showLoadingDialog();
                 newCall().url("http://192.168.90.54:8080/a/api/app/log/submit").body(new Param()
                        .add("channel", "ANDROID").add("serial", "2FwGqS703X386W5").add("content", "test")).call(new OkResponseListener() {
                    @Override
                    public void handleJsonSuccess(Call call, Response response, JSONObject json) throws Exception {
                        TS.show("错误信息已发送");
                    }

                    @Override
                    public void handleJsonError(Call call, Response response, JSONObject json) throws Exception {

                    }
                });
                break;

            default:
        }
    }

    @Override
    protected void onPhotoSelectResult(Intent data, ArrayList<TImage> images, TImage image) {
        super.onPhotoSelectResult(data, images, image);

        if (images != null && images.size() > 0) {
//            Uri uri = Uri.fromFile(new File(images.get(0).getCompressPath()));
//            L.e("path2: " + Tools.getRealFilePath(uri));
            File file = new File(images.get(0).getOriginalPath());
            L.e("length: " + file.length() + " exists:" + file.exists());
            rootHolder().setImageUrl(R.id.sivPhoto, file);
//            rootHolder().setImageUrl(R.id.sivPhoto, new File(images.get(0).getCompressPath()));
        }
    }

}
