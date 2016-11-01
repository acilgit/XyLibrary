package com.xycode.xylibrary.utils.cropUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.cropUtils.component.BaseImageView;
//import com.xycode.xylibrary.utils.cropUtils.util.BitmapOperator;

import java.io.File;


public class CropActivity extends BaseActivity {

//    public final int HARDWARE_ACCELERATED_MAX_SIZE = 2048;

    ViewGroup mAppBar;
    Button mBtnCancel;
    Button mBtnSave;
    ImageButton mBtnRotate;
    FrameLayout mBench;
    BaseImageView mImageView;

    Uri mOutUri;
    //
    int mOutWidth;
    //
    int mOutHeight;
    //
    boolean mIsSaving = false;

    Handler mHandler;
    private Bitmap bitmap;
    private BitmapDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        this.findViews();
        this.setListeners();
        this.init();
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    protected void findViews() {
        mAppBar = (ViewGroup) findViewById(R.id.appBar);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnRotate = (ImageButton) findViewById(R.id.btnRotate);
        mImageView = (BaseImageView) findViewById(R.id.imageView);
        mBench = (FrameLayout) findViewById(R.id.bench);
    }

    protected void setListeners() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClick();
            }
        });
        mBtnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRotateClick();
            }
        });
    }

    protected void init() {
        mHandler = new Handler();

        Intent intent = getIntent();
        Uri source = getIntent().getData();
        if (source == null) {
//            CropToast.show(CropActivity.this, getString(R.string.load_image_failed), mAppBar.getHeight());
            return;
        }
        mOutUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        if (mOutUri == null) {
//            CropToast.show(this, getString(R.string.should_specify_out_uri), mAppBar.getHeight());
            return;
        }
        mOutWidth = intent.getIntExtra(Crop.Extra.OUT_WIDTH, Crop.Default.OUT_WIDTH);
        mOutHeight = intent.getIntExtra(Crop.Extra.OUT_HEIGHT, Crop.Default.OUT_HEIGHT);

//        ByteArrayOutputStream os = ImageUtils.getByteArrayOutputStreamFromFile();
        bitmap = ImageUtils.resizeToBitmap(source.getPath(), Crop.Default.MAX_SIDE, Crop.Default.MINI_SIDE);
        drawable = new BitmapDrawable(getResources(), bitmap);
//        bitmap.recycle();
       /* try {
            drawable = BitmapOperator.createBitmapDrawable(this, source);
        } catch (IOException e) {
            e.printStackTrace();
//            CropToast.show(this, getString(R.string.load_image_failed), mAppBar.getHeight());
            finish();
            return;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
//            CropToast.show(this, getString(R.string.out_of_memory));
            return;
        }*/

       /* if (drawable.getIntrinsicWidth() < HARDWARE_ACCELERATED_MAX_SIZE
                && drawable.getIntrinsicHeight() < HARDWARE_ACCELERATED_MAX_SIZE) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            );
        }*/

        mImageView.setDrawable(drawable, mOutWidth, mOutHeight);
    }

    protected void onCancelClick() {
        if (mIsSaving) {
            return;
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }

    @Override
    protected boolean useEventBus() {
        return false;
    }

    protected void onSaveClick() {
        if (mIsSaving) {
            return;
        }

        mIsSaving = true;

        final Bitmap bitmap = mImageView.cropImage();

        if (bitmap == null) {
            mIsSaving = false;
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(mOutUri.getPath());
                final boolean saveResult = ImageUtils.saveBitmapToFile(getThis(), file, bitmap);
//                final BitmapOperator.SaveResult saveResult = BitmapOperator.saveToDisk(CropActivity.this, mOutUri, bitmap);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSaveResult(saveResult);
                    }
                });
            }
        }).start();
    }

    protected void onRotateClick() {//
        mImageView.rotate(90.0f);
    }

    protected void onSaveResult(boolean result) {
        mIsSaving = false;

        if (!result) {
//            CropToast.show(this, getString(R.string.save_image_failed), mAppBar.getHeight());
        } else {
            setResult(RESULT_OK, new Intent().setData(mOutUri));
            finish();
        }
    }
}
