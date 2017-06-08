package com.xycode.xylibrary.base;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;
import com.yalantis.ucrop.UCrop;

import java.io.Serializable;

public abstract class PhotoSelectBaseActivity extends BaseActivity {

    public static final String PARAM = "param";
    public static final String MULTI_SELECT = "multiSelect";

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROP = 3;

    private CropParam cropParam;
    private UCrop.Options options;

    @SaveState
    private Uri tempCropUri;
    @SaveState
    private boolean isCrop = false;
    @SaveState
    private boolean multiSelect = false;

/*    public static void startForResult(Activity activity, Class activityClass, boolean isCrop) {
        activity.startActivityForResult(new Intent(activity, activityClass).putExtra(IS_CROP, isCrop), REQUEST_CODE_PHOTO_SELECT);
    }*/

    public static void startForResult(BaseActivity activity, Class activityClass, ImageSelectorOptions options) {
        Intent intent = new Intent(activity, activityClass);
        intent.putExtra(MULTI_SELECT, true);
        activity.startActivityForResult(intent, REQUEST_CODE_MULTI_PHOTO_SELECT);
    }

    public static void startForResult(BaseActivity activity, Class activityClass, CropParam param) {
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

    protected void onCamera() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getThis(),
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.getTempImageUri());
                        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                    }

                    @Override
                    public void onDenied(String permission) {
                        permissionOnDenied(permission);
                    }
                });
    }

    protected void onAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final Uri[] resultUri = {null};
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    resultUri[0] = ImageUtils.getTempImageUri();
                    break;
                case REQUEST_CODE_ALBUM:
                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getThis(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            new PermissionsResultAction() {
                                @Override
                                public void onGranted() {
                                    resultUri[0] = Tools.getFilePathByUri(data.getData());
                                }

                                @Override
                                public void onDenied(String permission) {
                                    permissionOnDenied(permission);
                                }
                            });
                    break;
                case REQUEST_CODE_CROP:
                    isCrop = false;
                    resultUri[0] = tempCropUri;
                    break;
                default:
                    onResultFailure();
                    break;
            }
            if (isCrop) {
                tempCropUri = ImageUtils.getTempCropImageUri();
                UCrop uCrop = UCrop.of(resultUri[0], tempCropUri)
                        .withMaxResultSize(cropParam.outWidth, cropParam.outHeight);
                if (cropParam.aspectRatioX > 0 && cropParam.aspectRatioX > 0)
                    uCrop.withAspectRatio(cropParam.aspectRatioX, cropParam.aspectRatioY);
                if(options != null) uCrop.withOptions(options);

                uCrop.start(getThis(), REQUEST_CODE_CROP);
            } else {
                ImageUtils.removeFromFrescoCache(resultUri[0]);
                setResult(RESULT_OK, new Intent().setData(resultUri[0]));
                finish();
            }
        } else {
            onResultFailure();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    protected abstract void permissionOnDenied(String permission);

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
