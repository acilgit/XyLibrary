package com.xycode.xylibrary.okHttp;

import java.util.HashMap;

/**
 * Created by XY on 2016/7/11.
 */
public class Header extends HashMap<String, String> {

    public Header() {
        super();
    }

    public Header(String key, String value) {
        super();
        this.put(key, value);
    }

    public Header add(String key, String value) {
        this.put(key, value);
        return this;
    }

}