package com.xycode.xylibrary.okHttp;

import android.app.Activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by XY on 2017-06-20.
 */
public class CallItem {

    Call call;
    String id;
    String url;
    Param body;
    Header header;
    int method = OkHttp.POST;
    Map<String, File> files;
    Activity activity;
    OkResponseListener okResponseListener;
    OkFileHelper.FileProgressListener fileProgressListener;
    boolean addDefaultParams = true;
    boolean addDefaultHeader = true;

    /**
     * 进行请求
     *
     * @param okResponseListener
     */
    public Call call(OkResponseListener okResponseListener) {
        this.okResponseListener = okResponseListener;
        if (files != null) {
           call = OkHttp.uploadFiles(activity, url, files, body, header, addDefaultHeader, addDefaultParams, okResponseListener, fileProgressListener);
        } else {
            call = OkHttp.request(method, activity, url, body, addDefaultParams, header, addDefaultHeader, okResponseListener);
        }
        return call;
    }

    public CallItem get() {
        this.method = OkHttp.GET;
        return this;
    }

    public CallItem url(String url) {
        this.url = url;
        return this;
    }

    // get请求中会把Body中的数据拼接成地址
    public CallItem body(Param body) {
        this.body = body;
        return this;
    }

    public CallItem header(Header header) {
        this.header = header;
        return this;
    }

    public CallItem files(Map<String, File> files, OkFileHelper.FileProgressListener fileProgressListener) {
        this.files = files;
        this.fileProgressListener = fileProgressListener;
        return this;
    }

    public CallItem oneFile(String fileKey, File file, OkFileHelper.FileProgressListener fileProgressListener) {
        HashMap<String, File> files = new HashMap<>();
        files.put(fileKey, file);
        this.files = files;
        this.fileProgressListener = fileProgressListener;
        return this;
    }

    public CallItem addDefaultParams(boolean addDefaultParams) {
        this.addDefaultParams = addDefaultParams;
        return this;
    }

    public CallItem addDefaultHeader(boolean addDefaultHeader) {
        this.addDefaultHeader = addDefaultHeader;
        return this;
    }

    /**
     * 执行完call()后会返回call，但call也可能为空，请进行判断
     * @return
     */
    public Call getCall() {
        return call;
    }
}
