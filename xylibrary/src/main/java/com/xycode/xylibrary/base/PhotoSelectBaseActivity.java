package com.xycode.xylibrary.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.interfaces.PermissionListener;
import com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions;
import com.xycode.xylibrary.utils.ImageUtils;
import com.yalantis.ucrop.UCrop;

import java.io.Serializable;
import java.util.List;

public abstract class PhotoSelectBaseActivity extends XyBaseActivity {

    public static final String PARAM = "param";
    public static final String MULTI_SELECT = "multiSelect";

    /**
     * requestCode 拍照
     */
    private static final int REQUEST_CODE_CAMERA = 1;
    /**
     * requestCode 打开相册
     */
    private static final int REQUEST_CODE_ALBUM = 2;
    /**
     * requestCode 裁剪
     */
    private static final int REQUEST_CODE_CROP = 3;

    private CropParam cropParam;
    private UCrop.Options options;

    private static final int REQ_PERMISSION_CODE_TAKE_PHOTO = 103;

    @SaveState
    private Uri tempCropUri;
    @SaveState
    private boolean isCrop = false;
    @SaveState
    private boolean multiSelect = false;

/*    public static void startForResult(Activity activity, Class activityClass, boolean isCrop) {
        activity.startActivityForResult(new Intent(activity, activityClass).putExtra(IS_CROP, isCrop), REQUEST_CODE_PHOTO_SELECT);
    }*/

    public static void startForResult(XyBaseActivity activity, Class activityClass, ImageSelectorOptions options) {
        Intent intent = new Intent(activity, activityClass);
        intent.putExtra(MULTI_SELECT, true);
        activity.startActivityForResult(intent, REQUEST_CODE_MULTI_PHOTO_SELECT);
    }

    public static void startForResult(XyBaseActivity activity, Class activityClass, CropParam param) {
        Intent intent = new Intent(activity, activityClass);
        if (param == null) {
            param = new CropParam();
        }
        intent.putExtra(PARAM, param);
        activity.startActivityForResult(intent, REQUEST_CODE_PHOTO_SELECT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cropParam = (CropParam) getIntent().getSerializableExtra(PARAM);
        isCrop = (cropParam.outHeight > 0 && cropParam.outWidth > 0);
        options = getCropOptions();
    }

    /**
     * 请求权限并调用相机
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onCamera() {
        /*请求权限*/
        String[] requestPermissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermissions(requestPermissions, new PermissionListener() {
            @Override
            public void onGranted() {
                 /* 调用相机 */
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri tempImageUri = ImageUtils.getTempImageUri(getThis());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        });

    }

    /**
     * 打开相册
     */
    protected void onAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri[] resultUri = {null};

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    /* 拍照 拿到对应的那个地址 */
                    resultUri[0] = ImageUtils.getTempImageUri(getThis());
                    break;
                case REQUEST_CODE_ALBUM:

//                    resultUri[0] = Tools.getFilePathByUri(data.getData());
                    resultUri[0] = data.getData();

                    break;
                case REQUEST_CODE_CROP:
                    isCrop = false;
                    /* 裁剪后的图片uri 赋值给resultUri*/
                    resultUri[0] = tempCropUri;
                    break;
                default:
                    onResultFailure();
                    break;
            }

            // TODO: 2018/1/26 7.0模拟器中无法处理 裁剪 ，如果直接不裁剪的话就可以拿到文件地址，
            // TODO: 2018/1/26  裁剪之后的地址 裁剪之后的 RESULT_ERROR = 96 裁剪错误,直接不走了 else

            if (isCrop) {
                tempCropUri = ImageUtils.getTempCropImageUri(getThis());
                /*（结果图片地址，临时裁剪后的图片地址）进行裁剪*/
                UCrop uCrop = UCrop.of(resultUri[0], tempCropUri)
                        .withMaxResultSize(cropParam.outWidth, cropParam.outHeight);
                if (cropParam.aspectRatioX > 0 && cropParam.aspectRatioX > 0)
                    uCrop.withAspectRatio(cropParam.aspectRatioX, cropParam.aspectRatioY);
                if (options != null) uCrop.withOptions(options);
                uCrop.start(getThis(), REQUEST_CODE_CROP);


            } else {
                setResult(RESULT_OK, new Intent().setData(resultUri[0]));
                ImageUtils.removeFromFrescoCache(resultUri[0]);
                finish();
            }
        } else {
            onResultFailure();
        }
    }


    protected abstract UCrop.Options getCropOptions();

    protected void onResultFailure() {
        finish();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }


    public static class CropParam implements Serializable {
        public int aspectRatioX = 0;
        public int aspectRatioY = 0;
        public int outWidth = 512;
        public int outHeight = 512;

        public CropParam() {

        }

        public static CropParam out(int outWidth, int outHeight) {
            CropParam cropParam = new CropParam();
            cropParam.outWidth = outWidth;
            cropParam.outHeight = outHeight;
            return cropParam;
        }

        public CropParam ratio(int aspectRatioX, int aspectRatioY) {
            this.aspectRatioX = aspectRatioX;
            this.aspectRatioY = aspectRatioY;
            return this;
        }

    }
}
