package com.xycode.xylibrary.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import com.xycode.xylibrary.R;


public abstract class TabFragment extends Fragment {

    boolean mLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lazy, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
            tryLoad();
            onVisible();
        } else {
            onInvisible();
        }
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    /**
     * 当Fragment变为对用户可见时，该方法得到调用
     */
    protected void onVisible() {

    }

    /**
     * 当Fragment由对用户可见变为不可见时，该方法得到调用
     */
    protected void onInvisible() {

    }

    /**
     * 尝试加载懒内容
     */
    private void tryLoad() {
        if (mLoaded || !this.isAdded()) {// 已经加载过 || 尚未被添加到Activity
            return;
        }

        ImageView imageLoading = (ImageView)getView().findViewById(R.id.imageLoading);

        // 移除Loading标识
        ViewGroup group = (ViewGroup)getView();
        group.removeView(imageLoading);

        // 开始加载内容
        onLazyLoad();

        mLoaded = true;
    }

    /**
     * 在这里实现Fragment的懒加载
     */
    protected void onLazyLoad() {

    };

    protected ViewStub getViewStub() {
        return (ViewStub)getView().findViewById(R.id.viewStub);
    }


}
