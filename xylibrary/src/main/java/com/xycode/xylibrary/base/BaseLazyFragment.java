package com.xycode.xylibrary.base;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xycode.xylibrary.R;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.unit.MsgEvent;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Deprecated
public abstract class BaseLazyFragment extends Fragment {

    boolean loaded = false;
    private boolean loadFailed = false;

    protected XyBaseActivity getThis() {
        return (XyBaseActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;
    }

    @Override
    public void onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    /**
     * EventBus
     */
    protected abstract boolean useEventBus();

    public void postEvent(String eventName) {
        postEvent(eventName, null, null);
    }

    public void postEvent(String eventName, Interfaces.FeedBack feedBack) {
        postEvent(eventName, null, feedBack);
    }

    public void postEvent(String eventName, Object object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, String object) {
        postEvent(eventName, object, null);
    }

    public void postEvent(String eventName, Object object, Interfaces.FeedBack feedBack) {
        EventBus.getDefault().post(new MsgEvent(eventName, object, feedBack));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEvent event) {

    }

    @Subscribe
    public void onEventBackground(MsgEvent event) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!loaded && loadFailed) tryLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            tryLoad();
            if (loaded) onShow();
        } else {
            if (loaded) onHide();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     *
     */
    protected void onShow() {

    }

    protected void onHide() {

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

        SimpleDraweeView siv = new SimpleDraweeView(getContext());
        siv.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        siv.setAspectRatio(1);
        int side = Tools.dp2px(24);
        RelativeLayout.LayoutParams ivParam = new RelativeLayout.LayoutParams(side, side);
        ivParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        siv.setImageURI(ImageUtils.getResUri(R.mipmap.loading));
        rl.addView(siv, ivParam);
        ((ViewGroup) getView()).addView(rl, param);
        RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        siv.setAnimation(animation);
        animation.start();

        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
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
