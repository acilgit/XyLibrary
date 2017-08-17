package com.xycode.xylibrary.unit;

import java.io.Serializable;

/**
 * Created by XY on 2016-08-22.
 */
public class UrlData<T> implements Serializable{
    private Object url;
    private T data;

    public UrlData() {
    }

    public UrlData(Object url) {
        this.url = url;
    }

    public UrlData(Object url, T data) {
        this.url = url;
        this.data = data;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
