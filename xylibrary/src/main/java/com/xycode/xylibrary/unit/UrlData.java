package com.xycode.xylibrary.unit;

import java.io.Serializable;

/**
 * Created by XY on 2016-08-22.
 */
public class UrlData<T> implements Serializable{
    private String url;
    private T data;

    public UrlData() {
    }

    public UrlData(String url) {
        this.url = url;
    }

    public UrlData(String url, T data) {
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
