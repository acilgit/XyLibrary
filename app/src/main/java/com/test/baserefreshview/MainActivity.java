package com.test.baserefreshview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.xycode.xylibrary.adapter.XAdapter;
import com.xycode.xylibrary.xRefresher.XRefresher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private XRefresher xRefresher;
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
                holder.setText(R.id.tvText, "pos: " + (pos + 1)).setText(R.id.tvName, dataList.get(pos).getAddress());
            }

        };

        xRefresher.setup(this, adapter, true, new XRefresher.RefreshRequest<ContentBean>() {
            @Override
            public String setRequestParamsReturnUrl(HashMap<String, String> params) {
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



        }, 5);

    }
}
