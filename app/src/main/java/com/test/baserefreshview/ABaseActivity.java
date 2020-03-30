package com.test.baserefreshview;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import com.xycode.xylibrary.base.XyBaseActivity;

/**
 * Created by XY on 2016-11-01.
 */

public  class ABaseActivity extends XyBaseActivity {
    @Override
    protected int setActivityLayout() {
        return 0;
    }

    @Override
    protected void initOnCreate(Bundle savedInstanceState) {

    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }
}
