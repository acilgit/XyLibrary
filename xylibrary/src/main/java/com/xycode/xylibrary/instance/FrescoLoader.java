package com.xycode.xylibrary.instance;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.utils.ImageUtils;

import okhttp3.OkHttpClient;

/**
 * Created by XY on 2016-09-27.
 */

public class FrescoLoader {

    public static final String HTTP = "http";
    private static FrescoLoader instance;
    private OnFrescoListener onFrescoListener = null;
    private static ImageViewBitmapResize imageViewBitmapResize = null;


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

    public static void init(OkHttpClient client, OnFrescoListener onFrescoListener) {
        getInstance().onFrescoListener = onFrescoListener;
        if (Xy.getContext() != null) {
            ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(Xy.getContext(), client)
                    .build();
            Fresco.initialize(Xy.getContext(), config);
        }
    }

    /**
     * 取得图片地址
     * @param urlObject
     * @return
     */
    public static String getUrlInObject(Object urlObject) {
        if (urlObject instanceof String) {
            return (String) urlObject;
        }else if (getInstance().onFrescoListener != null) {
            String url = getInstance().onFrescoListener.getUrlInObject(urlObject);
            if (url != null && url.startsWith(HTTP)) return url;
        }
        return null;
    }

    /**
     * 取得图片缩略图地址
     * @param urlObject
     * @return
     */
    public static String getPreviewUrlInObject(Object urlObject) {
        if (getInstance().onFrescoListener != null) {
            String url = getInstance().onFrescoListener.getUrlPreviewInObject(urlObject);
            if (url != null && url.startsWith(HTTP)) return url;
        }
        return null;
    }

    /**
     * 接口外部调用方法
     * @param urlObject
     * @return
     */
    public static ResizeOptions getMaxResizeOptions(Object urlObject) {
        if (getInstance().onFrescoListener != null) {
            return getInstance().onFrescoListener.getMaxResizeOptions(urlObject);
        }
        return null;
    }

    /**
     * 所有的加载图片方法都请使用此方法及其重载方法
     * 如果图片过大，或无法确定图片尺寸，请使用SimpleDrawee进行加载，否则有OOM的风险
     * 传入Object来设置图片地址，和图片缩略图地址
     * 如果直接传入String则认定为地址，缩略图接口依然有效，请在{@link FrescoLoader#onFrescoListener 中getUrlPreviewInObject方法中设置 instanceof String 的情况}
     * @param imageView
     * @param urlObject
     */
    public static void setImageUrl(ImageView imageView, Object urlObject) {
        setImageUrl(imageView, urlObject, getMaxResizeOptions(urlObject));
    }

    public static void setImageUrl(ImageView imageView, Object urlObject, ResizeOptions resizeOptions) {
        if (imageView != null) {
            if (imageView instanceof SimpleDraweeView) {
                ImageUtils.setImageUriWithPreview((SimpleDraweeView) imageView, FrescoLoader.getUrlInObject(urlObject), FrescoLoader.getPreviewUrlInObject(urlObject), resizeOptions);
            } else {
                String url = FrescoLoader.getUrlInObject(urlObject);
                if (url == null) {
                    imageView.setImageURI(null);
                } else {
                    ImageUtils.loadBitmapFromFresco(Uri.parse(url), bitmap -> {
                        if (bitmap != null) {
                            if (imageViewBitmapResize != null) {
                                imageView.setImageBitmap(imageViewBitmapResize.resizeForImageView(bitmap));
                            } else {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            }
        }
    }

    public void setImageViewBitmapResize(ImageViewBitmapResize imageViewBitmapResize) {
        FrescoLoader.imageViewBitmapResize = imageViewBitmapResize;
    }

    public interface ImageViewBitmapResize {
        Bitmap resizeForImageView(Bitmap bitmap);
}
    public interface OnFrescoListener {
        /**
         * 根据传入的Object来确定图片要显示的最大尺寸
         * @param urlObject
         * @return
         */
        ResizeOptions getMaxResizeOptions(Object urlObject);

        /**
         * 从Object中取得图片的地址
         * 如果为String，则跳过此方法
         * @param urlObject
         * @return
         */
        String getUrlInObject(Object urlObject);

        /**
         * 从Object中取得图片缩略图地址
         * @param urlObject
         * @return
         */
        String getUrlPreviewInObject(Object urlObject);
    }

}
