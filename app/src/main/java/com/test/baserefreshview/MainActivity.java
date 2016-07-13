package com.test.baserefreshview;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.okHttp.OkFileHelper;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.Param;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private XRefresher xRefresher;
    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xRefresher = (XRefresher) findViewById(R.id.xRefresher);


        XAdapter<ContentBean> adapter = new XAdapter<ContentBean>(this, new ArrayList<ContentBean>(), R.layout.item_house) {

            @Override
            public void creatingHolder(CustomHolder holder, List<ContentBean> dataList, int viewType) {

            }

            @Override
            public void bindingHolder(CustomHolder holder, List<ContentBean> dataList, int pos) {
                holder.setText(R.id.tvName, "ad").setText(R.id.tvText, "ccc");
            }

            @Override
            protected void handleItemViewClick(CustomHolder holder, ContentBean item) {
                super.handleItemViewClick(holder, item);

            }
        };

        xRefresher.setup(this, adapter, true, new XRefresher.RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(Param params) {
//                params.add("a", "b");
                return "http://192.168.1.222:9000/append/store_recommend/sell_house_page";
            }

            @Override
            public List<ContentBean> setListData(JSONObject json) {
                return JSON.parseObject(json.toString(), ListBean.class).getContent().getContent();
            }

            @Override
            protected boolean ignoreSameItem(ContentBean newItem, ContentBean listItem) {
                return newItem.getId().equals(listItem.getId());
            }

        });
        xRefresher.refreshList();
    }


}
