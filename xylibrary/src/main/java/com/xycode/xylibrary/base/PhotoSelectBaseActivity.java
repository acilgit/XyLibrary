package com.xycode.xylibrary.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

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

    protected void onCamera() {
        //原始方式获取权限 如果没权限先去申请权限 待测试
      /*  if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_CODE_TAKE_PHOTO);

        } else {
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.getTempImageUri());
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        }*/

        AndPermission.with(this)
                .requestCode(REQ_PERMISSION_CODE_TAKE_PHOTO)
                .permission(Permission.CAMERA, Permission.STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        /* 调用相机 */
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri tempImageUri = ImageUtils.getTempImageUri(getThis());
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
                        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);

                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        /* 第一种：用默认的提示语。如果被不再询问并禁止，需要弹窗让他去设置页面开启 */
                        if (AndPermission.hasAlwaysDeniedPermission(getThis(), deniedPermissions)) {
                            AndPermission.defaultSettingDialog(getThis()).show();
                        }
                    }
                })
                /*rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    这样避免用户勾选不再提示，导致以后无法申请权限。你也可以不设置。*/
                .rationale((requestCode, rationale) -> {
                    /* 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。*/
                    AndPermission.rationaleDialog(getThis(), rationale).show();
                })
                .start();

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
            final Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
            onResultFailure();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //原始方式获取权限结果Result 可删除
     /*   if (requestCode == REQ_PERMISSION_CODE_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.getTempImageUri());
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            } else {
                // Permission Denied
                Toast.makeText(this, "未获取权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }*/
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
