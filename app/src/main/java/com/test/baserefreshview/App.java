package com.test.baserefreshview;

import android.app.Application;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.okHttp.OkHttp;
import com.xycode.xylibrary.okHttp.Param;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by XY on 2016/7/9.
 */
public class App extends Application {

    private static App instance;

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }

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
                Toast.makeText(App.getInstance(), R.string.ts_no_network, Toast.LENGTH_SHORT).show();
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
            public Param setDefaultParams(Param defaultParams) {
                defaultParams.put("sectionId", "");
                return defaultParams;
            }

            @Override
            public Param setDefaultHeader(Param defaultHeader) {
                return null;
            }

        });

    }
}
