package com.test.baserefreshview.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.baserefreshview.R;
import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.utils.downloadHelper.CompulsiveHelperActivity;
import com.xycode.xylibrary.utils.fileprovider.FileProvider7;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author thisfeng
 * @date 2018/1/25-下午2:05
 * <p>
 * 7.0使用FileProvider获取文件访问 测试
 * <p>
 * http://blog.csdn.net/lmj623565791/article/details/72859156#comments
 */

public class FitAndroid7Activity extends XyBaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    private static final int REQ_PERMISSION_CODE_SDCARD = 0X111;
    private static final int REQ_PERMISSION_CODE_TAKE_PHOTO = 0X112;

    private String mCurrentPhotoPath;
    private ImageView mIvPhoto;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_fit_android_7;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {


        mIvPhoto = (ImageView) findViewById(R.id.id_iv);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);

    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                //如果没权限先去申请权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_CODE_TAKE_PHOTO);

                } else {
                    takePhotoNoCompress();
                }
                break;
            case R.id.btn2:
                //如果没申请权限就去申请
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQ_PERMISSION_CODE_SDCARD);

                } else {
                    //有权限了
                    installApk();
                }
                break;
            case R.id.btn3:
                //权限框架
                update();
               /* AndPermission.with(this)
                        .requestCode(102)
                        .permission(Permission.STORAGE)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                L.e("拿到了PHONE+" + grantPermissions.toString());
                                update();
                            }

                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                // 第一种：用默认的提示语。
                                //如果被禁止，需要弹窗让他去设置页面开启
                                if (AndPermission.hasAlwaysDeniedPermission(getThis(), deniedPermissions)) {
                                    AndPermission.defaultSettingDialog(getThis()).show();
                                }
                            }
                        })
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                        // 你也可以不设置。
                        .rationale((requestCode, rationale) -> {
                            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(getThis(), rationale).show();
                        })
                        .start();*/

                break;

        }
    }

    private void update() {
//        String url = "https://cloud.ablegenius.com/files//null/image/2018_01_25/F002E9EBEE0B97BF.apk";
        String url = "https://cloud.ablegenius.com/files//null/image/2018_01_25/F7FF83E56AC9F3AD.apk";
        CompulsiveHelperActivity.Options options = new CompulsiveHelperActivity.Options(url);
//                        options.setTitle(bean.getData().getVersions());
        options.setMust(false).setIllustration("测试的");

                      /*  if (!isMust) options.setIgnoreCallback(obj -> {
                            //设置忽略版本的回调
                            SP.getPublicSP().put(SP.ignoreVersion, versionCode);
                        });
*/
        CompulsiveHelperActivity.update(this, new CompulsiveHelperActivity.CancelCallBack() {
            //下载过程中监听回调
            @Override
            public void onCancel(boolean must) {

            }

            @Override
            public void onFinish(boolean must) {

            }

            @Override
            public void onFailed(boolean must) {

            }

            @Override
            public void onDownLoad(int downLength, int fileLength) {

            }

            @Override
            public void onAbortUpdate() {

            }
        }, options);
    }


    private void installApk() {
        // 需要自己修改安装包路径  ／／外部储存目录
        File file = new File(Environment.getExternalStorageDirectory(),
                "app-debug.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(this,
                intent, "application/vnd.android.package-archive", file, true);

        startActivity(intent);
    }


    private void takePhotoNoCompress() {
        //去拍照 然后不压缩
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            //通过此方式获取文件地址
            Uri fileUri = FileProvider7.getUriForFile(this, file);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    /**
     * 原生处理请求权限后的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION_CODE_SDCARD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk();
            } else {
                // Permission Denied
                Toast.makeText(FitAndroid7Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == REQ_PERMISSION_CODE_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoNoCompress();
            } else {
                // Permission Denied
                Toast.makeText(FitAndroid7Activity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            mIvPhoto.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
        // else tip?

    }

}
