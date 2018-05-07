package com.test.baserefreshview.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import com.test.baserefreshview.dialogs.TipsDialog;
import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.interfaces.PermissionListener;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.downloadHelper.CompulsiveHelperActivity;
import com.xycode.xylibrary.utils.fileprovider.FileProvider7;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
                /**
                 * 如果没权限先去申请权限 原始方式
                 */
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_CODE_TAKE_PHOTO);

                } else {
                    takePhotoNoCompress();
                }
                break;
            case R.id.btn2:
                /**
                 * 封装Base中的权限获取方式，如果没申请权限就去申请   已测试版本 4.4 6.0 7.0 8.0 完全OJ8K
                 */
                String[] requestPermissions = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestRuntimePermissions(requestPermissions, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(FitAndroid7Activity.this, "Permission onGranted", Toast.LENGTH_SHORT).show();
                        installApk();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        // TODO: 2018/1/29 一般可具体的告诉用户为什么要获取这权限
                        new TipsDialog(getThis(), "权限获取失败", deniedPermissions.toString() + "权限被拒绝,您可以到应用管理中设置", "去设置", "取消", new Interfaces.OnCommitListener() {
                            @Override
                            public void onCommit(Object obj) {
                                Intent localIntent = new Intent();
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(localIntent);
                            }

                            @Override
                            public void onCancel(Object obj) {

                            }
                        }).show();

                    }
                });
                break;
            case R.id.btn3:
                //弹窗点击更新时，内部已经做了运行时权限框架
                update();
                break;
                default:

        }
    }

    private void update() {
//        String url = "https://cloud.ablegenius.com/files//null/image/2018_01_25/F002E9EBEE0B97BF.apk";
//        String url = "https://cloud.ablegenius.com/files//null/image/2018_01_25/F7FF83E56AC9F3AD.apk";
//        String url = "https://cloud.ablegenius.com/files//d4b09e2bf5244732be4f342e545964d3/image/2018_02_08/B8A43BBE73349696.apk";
        String url = "http://api.yisiduoer.com/duoerapp/estorer_1.0.0.apk";
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
        // 需要自己修改安装包路径  否则无用
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
        if (requestCode == REQ_PERMISSION_CODE_TAKE_PHOTO) {
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
