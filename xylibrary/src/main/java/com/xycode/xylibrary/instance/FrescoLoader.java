package com.xycode.xylibrary.instance;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.unit.WH;
import com.xycode.xylibrary.utils.ImageUtils;

/**
 * Created by XY on 2016-09-27.
 */

public class FrescoLoader {

    private static FrescoLoader instance;
    private OnFrescoListener onFrescoListener = null;

    public static FrescoLoader getInstance() {
        if (instance == null) {
            instance = new FrescoLoader();
        }
        return instance;
    }

    public static void init(OnFrescoListener onFrescoListener) {
        getInstance().onFrescoListener = onFrescoListener;
        if (Xy.getContext() != null) Fresco.initialize(Xy.getContext());
    }

    public static void init(boolean useOkHttp, OnFrescoListener onFrescoListener) {
        getInstance().onFrescoListener = onFrescoListener;
        if (Xy.getContext() != null) {
            if (useOkHttp) {
                ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                        .newBuilder(Xy.getContext(), OkHttp.getClient())
                        .build();
                Fresco.initialize(Xy.getContext(), config);
            } else {
                Fresco.initialize(Xy.getContext());
            }
        }
    }

    public static String getPreviewUri(String url) {
        if (url.startsWith("http") && getInstance().onFrescoListener != null) {
            return getInstance().onFrescoListener.getPreviewUri(url);
        }
        return null;
    }

    public static ResizeOptions getResizeOptions(String url) {
        if (url.startsWith("http") && getInstance().onFrescoListener != null) {
            return getInstance().onFrescoListener.getMaxResizeOptions(url);
        }
        return null;
    }

    public static void setImageURI(SimpleDraweeView simpleDraweeView, String url) {
        ImageUtils.setImageUriWithPreview(simpleDraweeView, url, FrescoLoader.getPreviewUri(url));
    }

    public interface OnFrescoListener {
        String getPreviewUri(String url);

        ResizeOptions getMaxResizeOptions(String url);
    }

}
