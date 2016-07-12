package com.xycode.xylibrary.okHttp;

import java.util.HashMap;

/**
 * Created by XY on 2016/7/11.
 */
public class Param extends HashMap<String, String> {

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