package com.xycode.xylibrary.okHttp;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xiuye on 2017/8/17.
 */

public class ResponseItem {

    private Response response;
    private Call call;
    private OkResponseListener okResponseListener;
    private JSONObject jsonObject;
    private String strResult;
    private String url;
    private String debugKey;
    private int resultCode = OkHttp.RESULT_BLANK;

    public ResponseItem(Response response, Call call, String url, OkResponseListener okResponseListener) {
        this.response = response;
        this.call = call;
        this.url = url;
        this.okResponseListener = okResponseListener;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public OkResponseListener getOkResponseListener() {
        return okResponseListener;
    }

    public void setOkResponseListener(OkResponseListener okResponseListener) {
        this.okResponseListener = okResponseListener;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getStrResult() {
        return strResult;
    }

    public void setStrResult(String strResult) {
        this.strResult = strResult;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDebugKey() {
        return debugKey;
    }

    public void setDebugKey(String debugKey) {
        this.debugKey = debugKey;
    }
}
