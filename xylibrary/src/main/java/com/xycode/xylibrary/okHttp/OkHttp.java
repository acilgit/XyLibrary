package com.xycode.xylibrary.okHttp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.base.BaseActivity;
import com.xycode.xylibrary.utils.LogUtil.JsonTool;
import com.xycode.xylibrary.utils.LogUtil.L;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by XY on 2016/7/7.
 */
public class OkHttp {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_URL_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MULTI_DATA = MediaType.parse("multipart/form-data; charset=utf-8");

    public static final String FILE = "file";
    public static final byte[] lock = new byte[0];

    public static final int RESULT_ERROR = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_VERIFY_ERROR = -1;
    public static final int RESULT_OTHER = 2;

    public static final int RESULT_PARSE_FAILED = 880;
    public static final int NETWORK_ERROR_CODE = 881;
    public static final int NO_NETWORK = 882;


    private static OkHttpClient client;
    private static IOkInit okInit;
    private static OkHttp.OkOptions okOptions;

    private static OkHttp instance;
//    private static Context application;

    private static Map<String, CallItem> callItems;

    public static OkHttp getInstance() {
        if (instance == null) {
            instance = new OkHttp();
        }
        return instance;
    }

    /**
     * init
     */
    public static void init(IOkInit iOkInit) {
        if (okInit == null) {
            okInit = iOkInit;
        }
    }

    public static void init(IOkInit iOkInit, OkOptions okOptions) {
        if (okInit == null) {
            okInit = iOkInit;
            OkHttp.okOptions = okOptions;
        }
    }

    /**
     * when use hotfix, set client in Application on local okHttp jar
     * @param iOkInit
     * @param client
     */
    public static void init(IOkInit iOkInit, OkHttpClient client) {
        okInit = iOkInit;
        OkHttp.client = client;
    }

    public static void setMaxTransFileCount(int max) {
        getClient().dispatcher().setMaxRequestsPerHost(max);
    }

    public static OkHttpClient getClient() {
        synchronized (lock) {
            if (client == null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .readTimeout(OkOptions.readTimeout, TimeUnit.SECONDS)
                        .connectTimeout(OkOptions.connectTimeout, TimeUnit.SECONDS)
                        .writeTimeout(OkOptions.writeTimeout, TimeUnit.SECONDS);
                if (okOptions != null) {
                    // 添加证书 或处理Builder
                    okOptions.setOkHttpBuilder(builder);
                }
                client = builder.build();
            }
            return client;
        }
    }

    public static Map<String, CallItem> getCallItems() {
        if (callItems == null) {
            callItems = new HashMap<>();
        } else {
            for (String key : callItems.keySet()) {
                if (callItems.get(key) == null) {
                    callItems.remove(key);
                }
            }
        }
        return callItems;
    }

    public static CallItem newCall(Activity activity) {
        CallItem callItem = new CallItem();
        callItem.id = String.valueOf(UUID.randomUUID());
        callItem.activity = activity;
        getCallItems().put(callItem.id, callItem);
        return callItem;
    }

    /**
     * 网络请求命令，只供CallItem调用
     *
     * @param activity
     * @param url
     * @param params
     * @param addDefaultParams
     * @param header
     * @param addDefaultHeader
     * @param okResponseListener
     * @return
     */
    static Call postOrGet(final Activity activity, String url, Param params, boolean addDefaultParams, Header header, boolean addDefaultHeader, final OkResponseListener okResponseListener) {
        final Request.Builder builder = new Request.Builder().url(url);
        StringBuffer sb = new StringBuffer();
        String logTitle;

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        try {
            if (params != null) {
                for (String key : params.keySet()) {
                    if (sb.length() == 0) sb.append("[Params]");
                    sb.append("\n  ").append(key).append(": ").append(params.get(key));
                    formBodyBuilder.add(key, params.get(key));
                }
            }
            if (addDefaultParams) {
                Param defaultParams = okInit.setDefaultParams(new Param());
                for (String key : defaultParams.keySet()) {
                    if (sb.length() == 0) sb.append("[Params]");
                    sb.append("\n  ").append(key).append(": ").append(defaultParams.get(key));
                    if (params != null && params.containsKey(key)) {
                        sb.append(" (ignored)");
                    } else {
                        formBodyBuilder.add(key, defaultParams.get(key));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("[Params Error] " + url, sb.toString());
            throw e;
        }

        boolean hasParams = sb.length() > 0;

        FormBody body = formBodyBuilder.build();

        if (hasParams) {
            builder.post(body);
            logTitle = "[POST] " + url;
        } else {
            builder.get();
            logTitle = "[GET] " + url;
        }

        Header defaultHeader = okInit.setDefaultHeader(new Header());
        if (header != null && header.size() > 0 || (addDefaultHeader && defaultHeader.size() > 0)){
            if (sb.length() > 0) sb.append("\n");
            sb.append("[Headers]");
        }
        if (addDefaultHeader && defaultHeader != null) {
            for (String key : defaultHeader.keySet()) {
                sb.append("\n  ").append(key).append(": ").append(defaultHeader.get(key));
                if (header != null && header.containsKey(key)) {
                    sb.append(" (ignored)");
                } else {
                    builder.addHeader(key, defaultHeader.get(key));
                }
            }
        }
        if (header != null) {
            for (String key : header.keySet()) {
                sb.append("\n  ").append(key).append(": ").append(header.get(key));
                builder.addHeader(key, header.get(key));
            }
        }
        final Request request = builder.build();
        final Call call = getClient().newCall(request);

        L.e(logTitle, sb.toString());

        new Thread(() -> {
            try {
                final Response response = call.execute();
                if (call.isCanceled()) L.e("[Call canceled] " + url, "");
                if (response != null) {
                    responseResult(response, call, okResponseListener, activity);
                    response.close();
                } else {
                    responseResultFailure(call, okResponseListener, activity);
                }
            } catch (IOException e) {
                e.printStackTrace();
                responseResultFailure(call, okResponseListener, activity);
            } finally {
            }
        }).start();
        return call;
    }

    /**
     * upload file，you can setMaxTransFileCount() to set max files upload thread pool size
     *
     * @param activity
     * @param url
     * @param files
     * @param params
     * @param header
     * @param addDefaultHeader
     * @param addDefaultParams
     * @param okResponseListener
     * @param fileProgressListener
     * @return
     */
    static Call uploadFiles(final Activity activity, String url, Map<String, File> files, Param params, final Header header, boolean addDefaultHeader, boolean addDefaultParams, final OkResponseListener okResponseListener, OkFileHelper.FileProgressListener fileProgressListener) {
        StringBuffer sb = new StringBuffer();
        String logTitle;
        logTitle = "[UPLOAD] " + url;
        if (files == null || files.size() == 0) {
            L.e(logTitle, "[Upload Canceled] fileSize: 0");
            return null;
        }
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                if (sb.length() == 0) sb.append("[Params]");
                sb.append("\n  ").append(key).append(": ").append(params.get(key));
                bodyBuilder.addFormDataPart(key, params.get(key));
            }
        }
        if (addDefaultParams) {
            Param defaultParams = okInit.setDefaultParams(new Param());
            for (String key : defaultParams.keySet()) {
                if (sb.length() == 0) sb.append("[Params]");
                sb.append("\n  ").append(key).append(": ").append(defaultParams.get(key));
                if (params != null && params.containsKey(key)) {
                    sb.append("(ignored)");
                } else {
                    bodyBuilder.addFormDataPart(key, defaultParams.get(key));
                }
            }
        }

        if (sb.length() > 0) sb.append("\n");
        sb.append("[Files]");
        for (String key : files.keySet()) {
            sb.append("\n  ").append(key).append(": ").append(files.get(key).getName());
            bodyBuilder.addFormDataPart(key, files.get(key).getName(), RequestBody.create(MEDIA_TYPE_MULTI_DATA, files.get(key)));
        }
        RequestBody requestBody = bodyBuilder.build();

        OkFileHelper.ProgressRequestBody progressRequestBody = new OkFileHelper.ProgressRequestBody(requestBody, fileProgressListener);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(progressRequestBody);
        // 添加Headers
        Header defaultHeader = okInit.setDefaultHeader(new Header());
        if (header != null && header.size() > 0 || (addDefaultHeader && defaultHeader != null && defaultHeader.size() > 0)) {
            sb.append("[Headers]");
            if (addDefaultHeader && defaultHeader != null) {
                for (String key : defaultHeader.keySet()) {
                    sb.append("\n  ").append(key).append(": ").append(defaultHeader.get(key));
                    if (header != null && header.containsKey(key)) {
                        sb.append("(ignored)");
                    } else {
                        requestBuilder.addHeader(key, defaultHeader.get(key));
                    }
                }
            }
            if (header != null) {
                for (String key : header.keySet()) {
                    sb.append("\n  ").append(key).append(": ").append(header.get(key));
                    requestBuilder.addHeader(key, header.get(key));
                }
            }
        }

        Request request = requestBuilder.build();

        Call call = OkHttp.getClient().newCall(request);

        L.e(logTitle, sb.toString());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response != null) {
                    responseResult(response, call, okResponseListener, activity);
                    response.close();
                } else {
                    responseResultFailure(call, okResponseListener, activity);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                responseResultFailure(call, okResponseListener, activity);
            }

        });
        return call;
    }


    /**
     * when response success
     *
     * @param response
     * @param call
     * @param okResponseListener
     */
    private static void responseResult(final Response response, final Call call, final OkResponseListener okResponseListener, Activity activity) {
        if (response.isSuccessful()) {
            String responseStr = "";
            try {
                final String strResult = response.body().string();
                responseStr = strResult;
                final JSONObject jsonObject = JSON.parseObject(strResult);
                final int resultCode = okInit.judgeResultWhenFirstReceivedResponse(call, response, jsonObject);
                if (okInit.resultSuccessByJudge(call, response, jsonObject, resultCode)) {
                    L.e("[resultJudgeFailed] " + call.request().url().url().toString(), JsonTool.stringToJSON(strResult));
                    BaseActivity.dismissLoadingDialogByManualState();
                    return;
                }
                if (call.isCanceled() || okResponseListener == null) {
                    BaseActivity.dismissLoadingDialogByManualState();
                    return;
                }
                try {
                    // 先在当前线程中处理
                    okResponseListener.handleSuccessInBackground(call, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BaseActivity.dismissLoadingDialogByManualState();
                // 如果传入Activity，则在主线程中处理内容
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        handleResultWithResultCode(response, call, okResponseListener, strResult, jsonObject, resultCode);
                    });
                } else {
                    handleResultWithResultCode(response, call, okResponseListener, strResult, jsonObject, resultCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                BaseActivity.dismissLoadingDialogByManualState();
                final String parseErrorResult = responseStr;
                L.e("[JsonParseFailed] " + call.request().url().url().toString(), "[Error]\n" + e.getMessage() + "\n[Result]\n " + responseStr);
                okInit.judgeResultParseResponseFailed(call, parseErrorResult, e);
                if (call.isCanceled() || okResponseListener == null) return;
                // 如果传入Activity，则在主线程中处理内容
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        try {
                            okResponseListener.handleParseError(call, parseErrorResult);
                            okResponseListener.handleAllFailureSituation(call, RESULT_PARSE_FAILED);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                } else {
                    try {
                        okResponseListener.handleParseError(call, parseErrorResult);
                        okResponseListener.handleAllFailureSituation(call, RESULT_PARSE_FAILED);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
        } else {
            BaseActivity.dismissLoadingDialogByManualState();
            L.e("[NetworkErrorCode: " + response.code() + "] " + call.request().url().url().toString(), "");
            okInit.receivedNetworkErrorCode(call, response);
            if (call.isCanceled()) return;
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    try {
                        okResponseListener.handleResponseFailure(call, response);
                        okResponseListener.handleAllFailureSituation(call, NETWORK_ERROR_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                try {
                    okResponseListener.handleResponseFailure(call, response);
                    okResponseListener.handleAllFailureSituation(call, NETWORK_ERROR_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 处理responseResult成功的内容
     *
     * @param response
     * @param call
     * @param okResponseListener
     * @param strResult
     * @param jsonObject
     * @param resultCode
     */
    private static void handleResultWithResultCode(Response response, Call call, OkResponseListener okResponseListener, String strResult, JSONObject jsonObject, int resultCode) {
        switch (resultCode) {
            case RESULT_SUCCESS:
                L.e("[Success] " + call.request().url().url().toString(), JsonTool.stringToJSON(strResult));
                try {
                    okResponseListener.handleJsonSuccess(call, response, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case RESULT_ERROR:
                L.e("[Error] " + call.request().url().url().toString(), strResult);
                try {
                    okResponseListener.handleJsonError(call, response, jsonObject);
                    okResponseListener.handleAllFailureSituation(call, resultCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case RESULT_VERIFY_ERROR:
                L.e("[VerifyError] " + call.request().url().url().toString(), strResult);
                try {
                    okResponseListener.handleJsonVerifyError(call, response, jsonObject);
                    okResponseListener.handleAllFailureSituation(call, resultCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                L.e("[OtherResultCode: " + resultCode + "] " + call.request().url().url().toString(), JsonTool.stringToJSON(strResult));
                try {
                    okResponseListener.handleJsonOther(call, response, jsonObject);
                    okResponseListener.handleAllFailureSituation(call, resultCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    /**
     * when response failure or call cancel
     *
     * @param call
     * @param okResponseListener
     */
    private static void responseResultFailure(final Call call, final OkResponseListener okResponseListener, Activity activity) {
        okInit.networkError(call, call.isCanceled());
        L.e("[networkError] " + call.request().url().url().toString(), "");
        BaseActivity.dismissLoadingDialogByManualState();
        if (okResponseListener != null && activity != null) {
            if (call.isCanceled()) return;
            activity.runOnUiThread(() -> {
                try {
                    okResponseListener.handleNoServerNetwork(call, call.isCanceled());
                    okResponseListener.handleAllFailureSituation(call, NO_NETWORK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    public static abstract class OkResponseListener implements IOkResponseListener {

        protected void handleJsonVerifyError(Call call, Response response, JSONObject json) throws Exception {

        }

        protected void handleJsonOther(Call call, Response response, JSONObject json) throws Exception {

        }

        protected void handleParseError(Call call, String responseResult) throws Exception {

        }

        protected void handleNoServerNetwork(Call call, boolean isCanceled) throws Exception {

        }

        protected void handleResponseFailure(Call call, Response response) throws Exception {

        }

        protected void handleAllFailureSituation(Call call, int resultCode) throws Exception {

        }

        /**
         * 成功时，在返回主线程前进行处理操作
         *
         * @param call
         * @param json
         * @throws Exception
         */
        protected void handleSuccessInBackground(Call call, JSONObject json) throws Exception {

        }


    }

    /**
     * 在主线程中操作返回的JSON
     */
    interface IOkResponseListener {
        void handleJsonSuccess(Call call, Response response, JSONObject json) throws Exception;

        void handleJsonError(Call call, Response response, JSONObject json) throws Exception;
    }

    public interface IOkInit {
        /**
         * the first time the response got from internet
         * 0：RESULT_ERROR ;
         * 1：RESULT_SUCCESS ;
         * -1：RESULT_VERIFY_ERROR;
         * 2: RESULT_OTHER ;
         * at IOkResponse interface callback
         *
         * @param call
         * @param response
         * @param json
         * @return
         */
        int judgeResultWhenFirstReceivedResponse(Call call, Response response, JSONObject json);

        /**
         * no network or  or call back cancel
         *
         * @param call
         * @param isCanceled
         */
        void networkError(Call call, boolean isCanceled);

        /**
         * after judgeResultWhenFirstReceivedResponse
         * result code not in  [200...300)
         *
         * @param call
         * @param response
         */
        void receivedNetworkErrorCode(Call call, Response response);

        /**
         * after judgeResultWhenFirstReceivedResponse
         * result is SUCCESS
         * returns ---
         * false: go on callbacks
         * true：interrupt callbacks
         * 可在此方法保存资料到SQLite
         *
         * @param call
         * @param response
         * @param json
         * @param resultCode
         * @return
         */
        boolean resultSuccessByJudge(Call call, Response response, JSONObject json, int resultCode);

        /**
         * after judgeResultWhenFirstReceivedResponse
         * when parse JSON failed
         *
         * @param call
         * @param parseErrorResult
         */
        void judgeResultParseResponseFailed(Call call, String parseErrorResult, Exception e);

        /**
         * add defaultParams in param
         * when setFormBody requestBody
         *
         * @param defaultParams
         * @return
         */
        Param setDefaultParams(Param defaultParams);

        /**
         * add defaultHeader in header
         * when new a request
         *
         * @param defaultHeader
         * @return
         */
        Header setDefaultHeader(Header defaultHeader);

    }

    private abstract class XRequestBody extends RequestBody {
        @Override
        public MediaType contentType() {
            return OkOptions.mediaType;
        }
    }


    /**
     * okHttp属性设置
     */
    public static class OkOptions {

        public static long readTimeout = 60;
        public static long connectTimeout = 30;
        public static long writeTimeout = 120;
        public static MediaType mediaType = null;


        public OkOptions(long readTimeout, long connectTimeout, long writeTimeout) {
            OkOptions.readTimeout = readTimeout;
            OkOptions.connectTimeout = connectTimeout;
            OkOptions.writeTimeout = writeTimeout;
        }

        /**
         * 设置 Builder
         *
         * @param builder
         */
        public void setOkHttpBuilder(OkHttpClient.Builder builder) {

        }

        public static void setMediaType(MediaType mediaType) {
            OkOptions.mediaType = mediaType;
        }


    }

}
