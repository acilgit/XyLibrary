package com.xycode.xylibrary.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseLazyFragment extends Fragment {

    boolean loaded = false;
    private boolean loadFailed = false;

    protected Activity getThis() {
        return getActivity();
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
    protected abstract void onShow();
    protected abstract void onHide();

    /**
     *
     */
    private void tryLoad() {
        if (loaded || !this.isAdded()) {
            loadFailed = true;
            return;
        }
//        ImageView imageLoading = (ImageView)getView().findViewById(R.id.imageLoading);

        //
//        ViewGroup group = (ViewGroup)getView();
//        group.removeView(imageLoading);

        //
        onFirstShow();
        loaded = true;
    }

    protected abstract void onFirstShow();

  /*  protected ViewStub getViewStub() {
        return (ViewStub)getView().findViewById(R.id.viewStub);
    }*/


}
