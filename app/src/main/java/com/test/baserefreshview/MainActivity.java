package com.test.baserefreshview;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private XRefresher xRefresher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xRefresher = (XRefresher) findViewById(R.id.xRefresher);

        XAdapter<ContentBean> adapter = new XAdapter<ContentBean>(this, new ArrayList<ContentBean>(), R.layout.item_house) {

            private  int a;
            @Override
            public void creatingHolder(CustomHolder holder, List<ContentBean> dataList, int viewType) {
            }

            @Override
            public void bindingHolder(CustomHolder holder, List<ContentBean> dataList, int pos) {

            }
        };

        xRefresher.setup(this, adapter, true, new XRefresher.RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(OkHttp.Param params) {
//                params.add("a", "b");
                return "http://192.168.1.222:9000/append/store_recommend/sell_house_page";
            }

            @Override
            public List<ContentBean> setListData(JSONObject json) {
                return JSON.parseObject(json.toString(), ListBean.class).getContent().getContent();
            }

            @Override
            public boolean setIsLastPageWhenGotJson(JSONObject json) {
                return json.getJSONObject("content").getBoolean("lastPage");
            }

            @Override
            protected boolean ignoreSameItem(ContentBean newItem, ContentBean listItem) {
                return newItem.getId().equals(listItem.getId());
            }
        });

    }

}
