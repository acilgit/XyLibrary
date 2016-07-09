package com.test.baserefreshview.views;

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

    public void postForm(String url, RequestBody body, final OkResponse okResponse) {
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
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

    public RequestBody setFormBody(HashMap<String, String> params) {
        return setFormBody(params, true);
    }

    public RequestBody setFormBody(HashMap<String, String> params, boolean addDefaultParams) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        if (okInit != null && addDefaultParams) {
            HashMap<String, String> defaultParams = okInit.setDefaultParams(new HashMap<String, String>());
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
        HashMap<String, String> setDefaultParams(HashMap<String, String> defaultParams);

    }

}
