package com.xycode.xylibrary.instance;

import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.utils.ImageUtils;

/**
 * Created by XY on 2016-09-27.
 */

public class SetupFresco {

    private static SetupFresco instance;
    private OnFrescoListener onFrescoListener = null;

    public static SetupFresco getInstance() {
        if (instance == null) {
            instance = new SetupFresco();
        }
        return instance;
    }

    public static void init(OnFrescoListener onFrescoListener) {
        getInstance().onFrescoListener = onFrescoListener;
    }

    public static String getPreviewUri(String url) {
        if (url.startsWith("http") && getInstance().onFrescoListener != null) {
                return getInstance().onFrescoListener.getPreviewUri(url);
            }
        return null;
    }

    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url) {
        ImageUtils.setImageUriWithPreview(simpleDraweeView, url, SetupFresco.getPreviewUri(url));
    }

    public interface OnFrescoListener {
        String getPreviewUri(String url);
    }

}
