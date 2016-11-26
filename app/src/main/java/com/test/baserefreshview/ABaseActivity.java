package com.test.baserefreshview;

import android.support.v7.app.AlertDialog;

import com.xycode.xylibrary.base.BaseActivity;

/**
 * Created by XY on 2016-11-01.
 */

public abstract class ABaseActivity extends BaseActivity {

    @Override
    protected boolean useEventBus() {
        return false;
    }
}
