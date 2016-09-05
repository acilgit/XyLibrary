package com.xycode.xylibrary.okHttp;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xycode.xylibrary.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.Map;
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
    public static final MediaType MEDIA_TYPE_MULTI_DATA = MediaType.parse("multipart/form-data; charset=utf-8");

    public static final String FILE = "file";

    public static final int RESULT_ERROR = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_VERIFY_ERROR = -1;
    public static final int RESULT_OTHER = 2;

    public static final int RESULT_PARSE_FAILED = 880;
    public static final int NETWORK_ERROR_CODE = 881;
    public static final int NO_NETWORK = 882;

    public static final long READ_TIMEOUT = 30;
    public static final long CONNECT_TIMEOUT = 10;
    public static final long WRITE_TIMEOUT = 60;

    private static OkHttpClient client;
    private static IOkInit okInit;

    private static OkHttp instance;

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

    public static void setMaxTransFileCount(int max) {
        getClient().dispatcher().setMaxRequestsPerHost(max);
    }

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public static RequestBody setFormBody(Param params) {
        return setFormBody(params, true);
    }

    public static RequestBody setFormBody(Param params, boolean addDefaultParams) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        if (addDefaultParams) {
            Param defaultParams = okInit.setDefaultParams(new Param());
            for (String key : defaultParams.keySet()) {
                builder.add(key, defaultParams.get(key));
            }
        }
        return builder.build();
    }

    public Call get(String url, boolean addDefaultHeader, OkResponseListener okResponseListener) {
        return get(url, null, addDefaultHeader, okResponseListener);
    }

    public Call get(String url, Header header, boolean addDefaultHeader, final OkResponseListener okResponseListener) {
        return postOrGet(url, null, header, addDefaultHeader, okResponseListener, true);
    }

    public Call postForm(String url, @NonNull RequestBody body, OkResponseListener okResponseListener) {
        return postForm(url, body, null, true, okResponseListener);
    }

    public Call postForm(String url, @NonNull RequestBody body, boolean addDefaultHeader, OkResponseListener okResponseListener) {
        return postForm(url, body, null, addDefaultHeader, okResponseListener);
    }

    public Call postForm(String url, @NonNull RequestBody body, Header header, boolean addDefaultHeader, OkResponseListener okResponseListener) {
        return postOrGet(url, body, header, addDefaultHeader, okResponseListener, true);
    }

    public Call postForm(String url, RequestBody body, Header header, boolean addDefaultHeader, final OkResponseListener okResponseListener, boolean callbackInUIThread) {
        return postOrGet(url, body, header, addDefaultHeader, okResponseListener, callbackInUIThread);
    }

    private Call postOrGet(String url, final RequestBody body, final Header header, boolean addDefaultHeader, final OkResponseListener okResponseListener, boolean callbackInUIThread) {
        final Request.Builder builder = new Request.Builder().url(url);
        if (body != null) {
            builder.post(body);
        } else {
            builder.get();
        }
        if (addDefaultHeader) {
            Header defaultHeader = okInit.setDefaultHeader(new Header());
            for (String key : defaultHeader.keySet()) {
                builder.addHeader(key, defaultHeader.get(key));
            }
        }
        if (header != null) {
            for (String key : header.keySet()) {
                builder.addHeader(key, header.get(key));
            }
        }
        final Request request = builder.build();
        final Call call = getClient().newCall(request);

        if (false) {
          /*  final Handler handler = new Handler(Looper.myLooper());

            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = call.execute();
                            if (response != null) {
                                responseResult(response, call, okResponseListener, handler);
                            } else {
                                responseResultFailure(call, okResponseListener, handler);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            responseResultFailure(call, okResponseListener, handler);
                        }
                    }
                });
            } else {
                try {
                    Response response = call.execute();
                    if (response != null) {
                        responseResult(response, call, okResponseListener, handler);
                    } else {
                        responseResultFailure(call, okResponseListener, handler);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    responseResultFailure(call, okResponseListener, handler);
                }
            }*/
        } else {
            new Thread(new Runnable() {
                final Handler handler = new Handler(Looper.getMainLooper());

                @Override
                public void run() {
                    try {
                        final Response response = call.execute();
                        if (response != null) {
                            responseResult(response, call, okResponseListener, handler);
                            response.close();
                        } else {
                            responseResultFailure(call, okResponseListener, handler);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        responseResultFailure(call, okResponseListener, handler);
                    } finally {
                    }
                }
            }).start();
        }

        return call;
    }

    /**
     * upload file，you can setMaxTransFileCount() to set max files upload thread pool size
     *
     * @param url
     * @param file
     * @param okResponseListener
     * @param fileProgressListener
     */
    public static Call uploadFile(String url, File file, final OkResponseListener okResponseListener, OkFileHelper.FileProgressListener fileProgressListener) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(FILE, file.getName(), RequestBody.create(MEDIA_TYPE_MULTI_DATA, file))
                .build();
        OkFileHelper.ProgressRequestBody progressRequestBody = new OkFileHelper.ProgressRequestBody(requestBody, fileProgressListener);
        Request request = new Request.Builder()
                .url(url)
                .post(progressRequestBody)
                .build();
        Call call = OkHttp.getClient().newCall(request);
        final Handler handler = new Handler(Looper.getMainLooper());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response != null) {
                    responseResult(response, call, okResponseListener, handler);
                    response.close();
                } else {
                    responseResultFailure(call, okResponseListener, handler);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                responseResultFailure(call, okResponseListener, handler);
            }
        });
        return call;
    }


    public static Call uploadFileWithParams(String url, Map<String, File> files, Param param, final OkResponseListener okResponseListener, OkFileHelper.FileProgressListener fileProgressListener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : param.keySet()) {
            builder.addFormDataPart(key, param.get(key));
        }
        for (String key : files.keySet()) {
            builder.addFormDataPart(key, files.get(key).getName(), RequestBody.create(MEDIA_TYPE_MULTI_DATA, files.get(key)));
        }

        RequestBody requestBody = builder.build();
        OkFileHelper.ProgressRequestBody progressRequestBody = new OkFileHelper.ProgressRequestBody(requestBody, fileProgressListener);
        Request request = new Request.Builder()
                .url(url)
                .post(progressRequestBody)
                .build();
        Call call = OkHttp.getClient().newCall(request);
        final Handler handler = new Handler(Looper.getMainLooper());
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response != null) {
                    responseResult(response, call, okResponseListener, handler);
                    response.close();
                } else {
                    responseResultFailure(call, okResponseListener, handler);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                responseResultFailure(call, okResponseListener, handler);
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
    private static void responseResult(final Response response, final Call call, final OkResponseListener okResponseListener, Handler handler) {
        BaseActivity.dismissLoadingDialogByManualState();
        if (response == null) {
            okInit.networkError(call, call.isCanceled());
            if (okResponseListener != null) {
                okResponseListener.handleAllFailureSituation(call, NO_NETWORK);
            }
        } else if (response.isSuccessful()) {
            try {
                String strResult = response.body().string();
                final JSONObject jsonObject = JSON.parseObject(strResult);
                final int resultCode = okInit.judgeResultWhenFirstReceivedResponse(call, response, jsonObject);
                if (okInit.resultSuccessByJudge(call, response, jsonObject, resultCode)) {
                    BaseActivity.dismissLoadingDialogByManualState();
                    return;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (resultCode) {
                            case RESULT_SUCCESS:
                                if (okResponseListener != null)
                                    okResponseListener.handleJsonSuccess(call, response, jsonObject);
                                break;
                            case RESULT_ERROR:
                                if (okResponseListener != null) {
                                    okResponseListener.handleJsonError(call, response, jsonObject);
                                    okResponseListener.handleAllFailureSituation(call, resultCode);
                                }
                                break;
                            case RESULT_VERIFY_ERROR:
                                if (okResponseListener != null) {
                                    okResponseListener.handleJsonVerifyError(call, response, jsonObject);
                                    okResponseListener.handleAllFailureSituation(call, resultCode);
                                }
                                break;
                            default:
                                if (okResponseListener != null) {
                                    okResponseListener.handleJsonOther(call, response, jsonObject);
                                    okResponseListener.handleAllFailureSituation(call, resultCode);
                                }
                                break;
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                okInit.judgeResultParseResponseFailed(call, response, e);
                if (okResponseListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            okResponseListener.handleParseError(call, response);
                            okResponseListener.handleAllFailureSituation(call, RESULT_PARSE_FAILED);
                        }
                    });
                }
            }
        } else {
            okInit.receivedNetworkErrorCode(call, response);
            if (okResponseListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        okResponseListener.handleResponseFailure(call, response);
                        okResponseListener.handleAllFailureSituation(call, NETWORK_ERROR_CODE);
                    }
                });
            }
        }
    }

    /**
     * when response failure or call cancel
     *
     * @param call
     * @param okResponseListener
     */
    private static void responseResultFailure(final Call call, final OkResponseListener okResponseListener, Handler handler) {
        okInit.networkError(call, call.isCanceled());
        BaseActivity.dismissLoadingDialogByManualState();
        if (okResponseListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    okResponseListener.handleNoServerNetwork(call, call.isCanceled());
                }
            });

        }
    }

    public static abstract class OkResponseListener implements IOkResponseListener {

        protected void handleJsonVerifyError(Call call, Response response, JSONObject json) {

        }

        protected void handleJsonOther(Call call, Response response, JSONObject json) {

        }

        protected void handleParseError(Call call, Response response) {

        }

        protected void handleNoServerNetwork(Call call, boolean isCanceled) {

        }

        protected void handleResponseFailure(Call call, Response response) {

        }

        protected void handleAllFailureSituation(Call call, int resultCode) {

        }
    }

    interface IOkResponseListener {
        void handleJsonSuccess(Call call, Response response, JSONObject json);

        void handleJsonError(Call call, Response response, JSONObject json);
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
         * @param response
         */
        void judgeResultParseResponseFailed(Call call, Response response, Exception e);

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


}
