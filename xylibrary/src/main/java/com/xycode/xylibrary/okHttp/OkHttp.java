package com.xycode.xylibrary.okHttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by XY on 2016/7/7.
 */
public class OkHttp {
    public static final int RESULT_ERROR = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_VERIFY_ERROR = -1;
    public static final int RESULT_OTHER = 2;

    private OkHttpClient client;
    private static IOkInit okInit;

    private static OkHttp instance;

    public static OkHttp getInstance() {
        if (instance == null) {
            instance = new OkHttp();
        }
        return instance;
    }

    /**
     * 初始化
     *
     */
    public static void init(IOkInit iOkInit) {
        if (okInit == null) {
            okInit = iOkInit;
        }
    }

    public OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public void get(String url, OkResponse okResponse) {
        postForm(url, null, okResponse);
    }

    public void postForm(String url, RequestBody body, final OkResponse okResponse) {
        Request.Builder builder = new Request.Builder().url(url);
        if(body != null) builder.post(body);
        final Request request = builder.build();
        Call call = getClient().newCall(request);
        try {
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        try {
                            String strResult = response.body().string();
                            JSONObject jsonObject = JSON.parseObject(strResult);
                            int resultCode = okInit.judgeResponse(call, response, jsonObject);
                            if (okInit.responseSuccess(call, response, jsonObject, resultCode)) return;
                            switch (resultCode) {
                                case RESULT_SUCCESS:
                                    okResponse.handleJsonSuccess(call, response, jsonObject);
                                    break;
                                case RESULT_ERROR:
                                    okResponse.handleJsonError(call, response, jsonObject);
                                    break;
                                case RESULT_VERIFY_ERROR:
                                    okResponse.handleJsonVerifyError(call, response, jsonObject);
                                    break;
                                default:
                                    okResponse.handleJsonOther(call, response, jsonObject);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            okInit.parseResponseFailed(call, response);
                            okResponse.handleParseError(call, response);
                        }
                    } else {
                        okInit.networkError(call, response);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    okInit.noNetwork(call);
                    okResponse.handleNoNetwork(call);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RequestBody setFormBody(Param params) {
        return setFormBody(params, true);
    }

    public static RequestBody setFormBody(Param params, boolean addDefaultParams) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        if (okInit != null && addDefaultParams) {
            Param defaultParams = okInit.setDefaultParams(new Param());
            for (String key : defaultParams.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        return builder.build();
    }

    public static abstract class OkResponse implements IOkResponseListener {

        protected void handleJsonVerifyError(Call call, Response response, JSONObject json) {

        }

        protected void handleJsonOther(Call call, Response response, JSONObject json) {

        }

        protected void handleParseError(Call call, Response response) {

        }

        protected void handleNoNetwork(Call call) {

        }
    }

    interface IOkResponseListener {
        void handleJsonSuccess(Call call, Response response, JSONObject json);

        void handleJsonError(Call call, Response response, JSONObject json);
    }

   public interface IOkInit {
        /**
         * 对返回的结果进行判断
         * 0：结果错误
         * 1：正确
         * 2：其它
         * 在IOkResponse接口中处理上面的情况
         *
         * @param call
         * @param response
         * @param json
         * @return
         */
        int judgeResponse(Call call, Response response, JSONObject json);

        /**
         * 没有网络
         *
         * @param call
         */
        void noNetwork(Call call);

        /**
         * 返回结果不为 200
         *
         * @param call
         * @param response
         */
        void networkError(Call call, Response response);

        /**
         * 结果成功后的操作，
         * false：断续执行下面的操作
         * true：中断操作
         *
         * @param call
         * @param response
         * @param json
         * @param resultCode
         * @return
         */
        boolean responseSuccess(Call call, Response response, JSONObject json, int resultCode);

        /**
         * 解释JSON时失败，如果不为JSON
         *
         * @param call
         * @param response
         */
        void parseResponseFailed(Call call, Response response);

        /**
         * 设置默认参数
         * 当使用setFormBody等方法设置requestBody时，可选是否加入默认参数
         *
         * @param defaultParams
         * @return
         */
        Param setDefaultParams(Param defaultParams);

    }

    public static class Param extends HashMap<String, String> {

        public Param() {
            super();
        }


        public Param(String key, String value) {
            super();
            this.put(key, value);
        }


        public Param add(String key, String value) {
            this.put(key, value);
            return this;
        }
    }


}
