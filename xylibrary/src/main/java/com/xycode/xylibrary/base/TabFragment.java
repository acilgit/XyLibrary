package com.xycode.xylibrary.base;


import android.app.Activity;
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

    protected Activity getThis() {
        return getActivity();
    }

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
     *
     */
    protected void onVisible() {

    }

    /**
     *
     */
    protected void onInvisible() {

    }

    /**
     *
     */
    private void tryLoad() {
        if (mLoaded || !this.isAdded()) {
            return;
        }

        ImageView imageLoading = (ImageView)getView().findViewById(R.id.imageLoading);

        //
        ViewGroup group = (ViewGroup)getView();
        group.removeView(imageLoading);

        //
        onLazyLoad();

        mLoaded = true;
    }

    /**
     *
     */
    protected void onLazyLoad() {

    }

    protected ViewStub getViewStub() {
        return (ViewStub)getView().findViewById(R.id.viewStub);
    }


}
