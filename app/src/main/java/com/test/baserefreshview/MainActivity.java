package com.test.baserefreshview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.baserefreshview.ListBean.Content.ContentBean;
import com.test.baserefreshview.views.OkHttp;
import com.test.baserefreshview.views.XAdapter;
import com.test.baserefreshview.views.XRefresher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private XRefresher<ContentBean> xRefresher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttp.init(new OkHttp.IOkInit() {
            @Override
            public int judgeResponse(Call call, Response response, JSONObject json) {
                String resultCode = json.getString("resultCode");
                if ("1".equals(resultCode)) {
                    return OkHttp.RESULT_SUCCESS;
                } else  if ("0".equals(resultCode)) {
                    return OkHttp.RESULT_ERROR;
                }else  if ("-1".equals(resultCode)) {
                    return OkHttp.RESULT_VERIFY_ERROR;
                }
                return OkHttp.RESULT_OTHER;
            }

            @Override
            public void noNetwork(Call call) {

            }

            @Override
            public void networkError(Call call, Response response) {

            }

            @Override
            public boolean responseSuccess(Call call, Response response, JSONObject json, int resultCode) {
                switch (resultCode) {
                    case OkHttp.RESULT_VERIFY_ERROR:
                        return true;
                }
                return false;
            }

            @Override
            public void parseResponseFailed(Call call, Response response) {

            }

            @Override
            public HashMap<String, String> setDefaultParams(HashMap<String, String> defaultParams) {
                return defaultParams;
            }
        });

       RecyclerView rvMain = (RecyclerView) findViewById(R.id.rvMain);
       SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        rvMain.setLayoutManager(new LinearLayoutManager(this));

        XAdapter<ContentBean> adapter = new XAdapter<ContentBean>(this, new ArrayList<ContentBean>(), R.layout.item_house) {

            @Override
            public void creatingHolder(CustomHolder holder, List<ContentBean> dataList, int viewType) {

            }

            @Override
            public void bindingHolder(CustomHolder holder, List<ContentBean> dataList, int pos) {
                holder.setText(R.id.tvText, "pos: " + (pos + 1))
                .setText(R.id.tvName, dataList.get(pos).getAddress());
            }

            @Override
            protected void bindFooterView(CustomHolder holder, int footerState) {
                holder.getView(R.id.pbLoadMore).setVisibility(footerState == XAdapter.FOOTER_LOADING ? View.VISIBLE : View.GONE);
                holder.setText(R.id.tvLoading, footerState == XAdapter.FOOTER_LOADING ? "加载中..." : "加载更多");
            }
        };
        adapter.setFooterLayout(R.layout.layout_load_more);

        rvMain.setAdapter(adapter);
        xRefresher = new XRefresher<>(this, swipe, rvMain, true, new XRefresher.RefreshRequest<ContentBean>() {

            @Override
            public String setRequestParamsReturnUrl(HashMap<String, String> params) {
                return "http://192.168.1.233:9000/append/store_recommend/sell_house_page";
            }

            @Override
            public List<ContentBean> setListData(JSONObject json) {
                return JSON.parseObject(json.toString(), ListBean.class).getContent().getContent();
            }

            @Override
            public boolean setIsLastPageWhenGotJson(JSONObject json) {
                return json.getJSONObject("content").getBoolean("lastPage");
            }
        });

    }
}
