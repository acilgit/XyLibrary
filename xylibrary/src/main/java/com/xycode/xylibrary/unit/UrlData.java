package com.xycode.xylibrary.unit;

/**
 * Created by XY on 2016-08-22.
 */
public class UrlData {
    private String url;
    private Object data;

    public UrlData(String url) {
        this.url = url;
    }

    public UrlData(String url, Object data) {
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getData() {
        return data == null ? "" : data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
