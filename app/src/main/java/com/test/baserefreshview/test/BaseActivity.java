package com.test.baserefreshview.test;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import com.xycode.xylibrary.base.XyBaseActivity;

/**
 * @author thisfeng
 * @date 2018/2/6-下午5:11
 */

public class BaseActivity extends XyBaseActivity {
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
