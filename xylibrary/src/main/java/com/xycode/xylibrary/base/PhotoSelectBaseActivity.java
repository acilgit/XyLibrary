package com.xycode.xylibrary.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;
import com.xycode.xylibrary.utils.cropUtils.Crop;

public abstract class PhotoSelectBaseActivity extends BaseActivity {

    public static final String IS_CROP = "isCrop";

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROP = 3;

    private boolean isCrop = false;

    public static void startForResult(Activity activity, Class activityClass, boolean isCrop) {
        activity.startActivityForResult(new Intent(activity, activityClass).putExtra(IS_CROP, isCrop), REQUEST_CODE_PHOTO_SELECT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCrop = getIntent().getBooleanExtra(IS_CROP, false);
    }

    protected void onCamera() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getThis(),
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.getTempImageUri(getThis()));
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
                    resultUri[0] = ImageUtils.getTempImageUri(getThis());
                    break;
                case REQUEST_CODE_ALBUM:
                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getThis(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            new PermissionsResultAction() {
                                @Override
                                public void onGranted() {
                                    resultUri[0] = Tools.getFilePathByUri(getThis(), data.getData());
                                }

                                @Override
                                public void onDenied(String permission) {
                                    permissionOnDenied(permission);
                                }
                            });
                    break;
                case REQUEST_CODE_CROP:
                    isCrop = false;
                    resultUri[0] = ImageUtils.getTempCropImageUri(getThis());
                    break;
                default:
                    onResultFailure();
                    break;
            }
            if (isCrop) {
                Crop.of(resultUri[0], ImageUtils.getTempCropImageUri(getThis())).crop(getThis(), REQUEST_CODE_CROP);
            } else {
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

    protected void onResultFailure() {
        finish();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }
}
