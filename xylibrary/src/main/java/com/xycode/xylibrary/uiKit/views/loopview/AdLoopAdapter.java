package com.xycode.xylibrary.uiKit.views.loopview;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.uiKit.views.loopview.internal.BaseLoopAdapter;
import com.xycode.xylibrary.utils.ImageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdLoopAdapter
 */
class AdLoopAdapter extends BaseLoopAdapter {

    Map<Integer, SimpleDraweeView> imageViewList;

    public AdLoopAdapter(Context context, List<String> loopData, ViewPager viewPager) {
        super(context, loopData, viewPager);
        imageViewList = new HashMap<>();
    }

    /**
     * @param imageUrl
     */
    public View instantiateItemView(String imageUrl, int position) {
        SimpleDraweeView imageView = imageViewList.get(position);
        if (imageView == null) {
            imageView = new SimpleDraweeView(context);
            if (defaultImgId != 0)  imageView.getHierarchy().setPlaceholderImage(context.getResources().getDrawable(defaultImgId) ,ScalingUtils.ScaleType.FIT_XY);
            imageView.getHierarchy().setActualImageScaleType(imageScaleType);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            imageView.setAspectRatio(aspectRatio);
        }
        if (imageView.getTag()!=null && imageView.getTag().equals(imageUrl)) {
            return imageView;
        }
        if (onPreviewUrlListener != null) {
            ImageUtils.setImageUriWithPreview(imageView, imageUrl, onPreviewUrlListener.getPreviewUrl(imageUrl, position));
        } else {
            imageView.setImageURI(Uri.parse(imageUrl));
        }
        imageView.setTag(imageUrl);

        return imageView;
    }
}