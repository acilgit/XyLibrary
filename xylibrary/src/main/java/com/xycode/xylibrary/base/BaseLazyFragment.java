package com.xycode.xylibrary.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;


public abstract class BaseLazyFragment extends Fragment {

    boolean loaded = false;
    private boolean loadFailed = false;

    protected BaseActivity getThis() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!loaded && loadFailed) tryLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            tryLoad();
            onShow();
        } else {
            onHide();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     *
     */
    protected void onShow(){

    }
    protected void onHide(){

    }

    /**
     *
     */
    private void tryLoad() {
        if (loaded || !this.isAdded()) {
            loadFailed = true;
            return;
        }

        RelativeLayout rl = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        rl.setLayoutParams(param);


        SimpleDraweeView siv = new SimpleDraweeView(getContext());
        siv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        siv.setAspectRatio(1);
        int side = Tools.dp2px(getContext(), 16);
        RelativeLayout.LayoutParams ivParam = new RelativeLayout.LayoutParams(side, side);
        ivParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        siv.setLayoutParams(ivParam);
        siv.setImageURI(ImageUtils.getResUri(R.mipmap.loading));

        rl.addView(siv);
        ((ViewGroup) getView()).addView(rl);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.animator.rotate_loading);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        siv.setAnimation(animation);
        animation.start();


        onFirstShow();
        loaded = true;
        siv.clearAnimation();
        ((ViewGroup) getView()).removeView(rl);
    }

    protected abstract void onFirstShow();

  /*  protected ViewStub getViewStub() {
        return (ViewStub)getView().findViewById(R.id.viewStub);
    }*/


}
