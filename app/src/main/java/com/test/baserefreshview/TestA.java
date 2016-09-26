package com.test.baserefreshview;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.uiKit.views.nicespinner.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class TestA extends BaseActivity {
    NiceSpinner one;
    NiceSpinner two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        one = (NiceSpinner) findViewById(R.id.test_one);
        two = (NiceSpinner) findViewById(R.id.test_two);
        List<String> list = new ArrayList<>();
        list.add("或在在要要在");
        list.add("在在要要在");
        list.add("或要要在");
        list.add("或要要在");
        list.add("或要要在");
        list.add("或在在要在");
        list.add("要");
        one.attachDataSource(list);
        List<String> list2 = new ArrayList<>();
        list2.add("或在在要要在");
        list2.add("在在要要在");
        list2.add("或要要在");
        list2.add("或要要在");
        list2.add("或要要在");
        list2.add("或在在要在");
        list2.add("要");
        two.attachDataSource(list2);
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }
}
