package com.xycode.xylibrary.utils.cropUtils;


import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class Crop {

    public static final int REQUEST_PICK = 124;
    public static final int REQUEST_CROP = 215;

    interface Default {
        int OUT_WIDTH = 512;
        int OUT_HEIGHT = 512;
        int MAX_SIDE = 1600;
        int MINI_SIDE = 512;
    }

    public interface Extra {
        String OUT_WIDTH = "OutWidth";
        String OUT_HEIGHT = "outHeight";
        String MAX_SIDE = "maxSide";
        String MINI_SIDE = "minSide";
    }

    Uri source;
    Uri destination;
   public int outWidth = Default.OUT_WIDTH;
   public int outHeight = Default.OUT_HEIGHT;
   public int maxSide = Default.MAX_SIDE;
   public int minSide = Default.MINI_SIDE;

    public static Crop of(Uri sourceUri, Uri targetUri) {
        return new Crop(sourceUri, targetUri);
    }

    public static Crop size(int outWidth, int outHeight) {
        return new Crop(null, null).withSize(outWidth, outHeight);
    }

    public Crop(Uri source, Uri destination) {
        this.source = source;
        this.destination = destination;
    }

    public Crop withSize(int outWidth, int outHeight) {
        this.outWidth = outWidth;
        this.outHeight = outHeight;

        return this;
    }

    public Crop safeCrop(int maxSide, int minSide) {
        this.maxSide = maxSide;
        this.minSide = minSide;
        return this;
    }

    public void crop(Activity context) {
        crop(context, REQUEST_CROP);
    }

    public void crop(Fragment context) {
        crop(context, REQUEST_CROP);
    }

    public void crop(android.support.v4.app.Fragment context) {
        crop(context, REQUEST_CROP);
    }

    public void crop(Activity context, int requestCode) {
        context.startActivityForResult(getCropIntent(context), requestCode);
    }

    public void crop(Fragment context, int requestCode) {
        context.startActivityForResult(getCropIntent(context.getActivity()), requestCode);
    }

    public void crop(android.support.v4.app.Fragment context, int requestCode) {
        context.startActivityForResult(getCropIntent(context.getActivity()), requestCode);
    }

    public Intent getCropIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, CropActivity.class);
        intent.setData(source);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destination);
        intent.putExtra(Extra.OUT_WIDTH, outWidth);
        intent.putExtra(Extra.OUT_HEIGHT, outHeight);
        return intent;
    }

    public static void pick(Activity activity) {
        pick(activity, REQUEST_PICK);
    }

    public static void pick(Fragment fragment) {
        pick(fragment, REQUEST_PICK);
    }

    public static void pick(android.support.v4.app.Fragment fragment) {
        pick(fragment, REQUEST_PICK);
    }

    public static void pick(Activity activity, int requestCode) {
        Intent intent = getPickIntent();
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void pick(Fragment fragment, int requestCode) {
        Intent intent = getPickIntent();
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void pick(android.support.v4.app.Fragment fragment, int requestCode) {
        Intent intent = getPickIntent();
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Intent getPickIntent() {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        return intent;
    }
}
