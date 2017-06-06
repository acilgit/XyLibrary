package com.xycode.xylibrary.utils.crashUtil;

import android.app.Activity;
import android.support.annotation.LayoutRes;

/**
 * Created by XY on 2017-06-05.
 */

public interface ICrash {
    @LayoutRes int getLayoutId();

    void setViews(Activity activity);
}
