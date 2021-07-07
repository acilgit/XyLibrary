package com.test.baserefreshview;

import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.xycode.xylibrary.base.XyBaseActivity;
import com.xycode.xylibrary.base.BasePhotoActivity;
import com.xycode.xylibrary.unit.UrlData;

import java.util.List;

public class PhotoActivity extends BasePhotoActivity {

    public static void startThis(XyBaseActivity activity, String url) {
        if (url.isEmpty()) return;
        startThis(activity, PhotoActivity.class, url);
    }

    public static void startThis(XyBaseActivity activity, List<UrlData> urls, int pos) {
        if (urls.isEmpty()) return;
        startThis(activity, PhotoActivity.class, urls, pos);
    }

   /* public static void startThis(View view, List<UrlData> urls, int pos) {
        XyBaseActivity activity = (XyBaseActivity) view.getContext();
        SP.getPublic().put(SP.photos, JSON.toJSONString(urls));
        activity.startActivity(new Intent(activity, PhotoActivity.class).putExtra(Extras.position, pos).putExtra(Extras.animateFade, true));
    }*/

    @Override
    protected boolean useEventBus() {
        return false;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    @Override
    protected View addPageView(ViewGroup container, UrlData data) {
        return null;
    }

 /*   @Override
    protected boolean isShowDotsView() {
        return true;
    }*/

    @Override
    protected Options setDisplayOptions() {

        Options options = new Options();
        options.setShowDotsView(true);
        options.setBitmapFailureRes(R.mipmap.ic_launcher);
        return options;
    }
}
